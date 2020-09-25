package ru.java.mentor.oldranger.club.restcontroller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.chat.Message;
import ru.java.mentor.oldranger.club.model.media.FileInChat;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BanType;
import ru.java.mentor.oldranger.club.service.chat.ChatService;
import ru.java.mentor.oldranger.club.service.chat.MessageService;
import ru.java.mentor.oldranger.club.service.media.FileInChatService;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.utils.CheckFileTypeService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;
import ru.java.mentor.oldranger.club.service.utils.WritingBanService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/chat")
@Tag(name = "Chat")
public class ChatRestController {

    private final CheckFileTypeService checkFileTypeService;
    private ChatService chatService;
    private PhotoService photoService;
    private PhotoAlbumService albumService;
    private MessageService messageService;
    private SecurityUtilsService securityUtilsService;
    private WritingBanService writingBanService;
    private FileInChatService fileInChatService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get current user info", description = "Avatar and username", tags = {"Chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "User is not logged in")})
    @GetMapping(value = "/user", produces = {"application/json"})
    ResponseEntity<Map<String, String>> getUserInfo() {
        User user = securityUtilsService.getLoggedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Map<String, String> info = new HashMap<>();
        info.put("username", user.getNickName());
        info.put("ava", user.getAvatar().getSmall());
        return ResponseEntity.ok(info);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get online users", tags = {"Group chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Map userNickname:userId",
                    content = @Content(schema = @Schema(implementation = Map.class)))})
    @GetMapping(value = "/users", produces = {"application/json"})
    ResponseEntity<Map<String, Long>> getOnlineUsers() {
        return ResponseEntity.ok(messageService.getOnlineUsers());
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Is it forbidden to participate in the chat", tags = {"Group chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Boolean",
                    content = @Content(schema = @Schema(implementation = Boolean.class)))})
    @GetMapping("/isForbidden")
    ResponseEntity<Boolean> isForbidden() {
        boolean isForbidden = writingBanService.isForbidden(securityUtilsService.getLoggedUser(), BanType.ON_CHAT);
        return ResponseEntity.ok(isForbidden);
    }

    @Operation(summary = "Get last messages", description = "limit 20 messages", tags = {"Group chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Message.class)))),
            @ApiResponse(responseCode = "204", description = "Page parameter is greater than the total number of pages")})
    @GetMapping(value = "/messages", produces = {"application/json"})
    ResponseEntity<List<Message>> getLastMessages(@RequestParam(value = "page", required = false) Integer page) {

        if (page == null) page = 0;
        Pageable pageable = PageRequest.of(page, 20, Sort.by("id").descending());

        Chat chat = chatService.getChatById(1L);

        Page<Message> msgPage = messageService.getPagebleMessages(chat, pageable);
        if (msgPage.getTotalPages() > page) {
            return ResponseEntity.ok(msgPage.getContent());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Upload image", tags = {"Group Chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Map originalImg:fileName, thumbnailImg:fileName, fileName:fileName, filePath:filePath",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "200", description = "Map fileName:fileName, filePath:filePath",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "User is not logged in"),
            @ApiResponse(responseCode = "413", description = "File size must not exceed 20Mb")})
    @PostMapping(value = "/image", consumes = {"multipart/form-data"})
    ResponseEntity<Map<String, String>> processImage(@Parameter(description = "Image file", required = true)
                                                     @RequestParam("file-input") MultipartFile file) {
        Map<String, String> result = new HashMap<>();
        User user = securityUtilsService.getLoggedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (checkFileTypeService.isValidImageFile(file)) {
            PhotoAlbum album = chatService.getGroupChat().getPhotoAlbum();
            Photo photo = photoService.save(album, file, 0);
            result.put("originalImg", photo.getOriginal());
            result.put("thumbnailImg", photo.getSmall());
        } else {
            Chat chat = chatService.getGroupChat();
            FileInChat uploadFile = fileInChatService.save(file, chat.getId());
            result.put("fileName", uploadFile.getFileName());
            result.put("filePath", uploadFile.getFilePath());
        }
        return ResponseEntity.ok(result);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all photos from chat", tags = {"Group Chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Photo.class)))),
            @ApiResponse(responseCode = "401", description = "User is not logged in")})
    @GetMapping(value = "/photos")
    ResponseEntity<List<Photo>> getChatPhotos() {
        User user = securityUtilsService.getLoggedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Chat chat = chatService.getGroupChat();
        PhotoAlbum album = chat.getPhotoAlbum();
        return ResponseEntity.ok(albumService.getAllPhotosByAlbum(album));
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete chat messages",
            description = "Delete all chat messages if param all=true, or else delete messages older than month",
            tags = {"Group Chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "User is not logged in")})
    @DeleteMapping(value = "/messages")
    ResponseEntity<String> deleteMessages(@Parameter(description = "Delete all messages or messages that older than month", required = true)
                                          @RequestParam(value = "all") Boolean all) {
        User user = securityUtilsService.getLoggedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        messageService.deleteMessages(false, all, null);
        albumService.deleteAlbumPhotos(all, chatService.getGroupChat().getPhotoAlbum());
        return ResponseEntity.ok().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Change period for automatic deletion messages ",
            tags = {"Group Chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "User is not logged in"),
    })
    @GetMapping(value = "/messages/{days}")
    ResponseEntity<String> changeDeletionTime(@Parameter(description = "Changes the automatic message deletion period", required = true)
                                              @PathVariable Long days) {
        User user = securityUtilsService.getLoggedUser();
        if (user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        messageService.setOlderThan(days);
        return ResponseEntity.ok().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete chat message",
            tags = {"Group Chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "User is not logged in"),
            @ApiResponse(responseCode = "403", description = "Forbidden to delete")})
    @DeleteMapping(value = "/messages/{id}")
    ResponseEntity<String> deleteMessage(@Parameter(description = "Message id", required = true)
                                         @PathVariable Long id) {
        User user = securityUtilsService.getLoggedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean isModer = securityUtilsService.isModerator() || securityUtilsService.isAdmin();
        boolean isSender = messageService.findMessage(id).getSender().equals(user.getNickName());
        if (!isSender) {
            if (!isModer) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        messageService.deleteMessage(id);
        return ResponseEntity.ok().build();
    }
}