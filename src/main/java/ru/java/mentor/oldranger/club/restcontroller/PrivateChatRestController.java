package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.dto.PrivateChatDto;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.chat.Message;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserAvatar;
import ru.java.mentor.oldranger.club.service.chat.ChatService;
import ru.java.mentor.oldranger.club.service.chat.MessageService;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/private")
public class PrivateChatRestController {

    private ChatService chatService;
    private MessageService messageService;
    private UserService userService;
    private SecurityUtilsService securityUtilsService;
    private PhotoAlbumService albumService;
    private PhotoService photoService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get chat token", description = "Get chat token by user id", tags = {"Private Chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat token",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "204", description = "User not found")})
    @GetMapping(value = "/{id}")
    public ResponseEntity<String> getChatIdByUser(@PathVariable Long id,
                                                  @AuthenticationPrincipal User currentUser) {
        User user = userService.findById(id);
        if (user == null || currentUser == null) {
            return ResponseEntity.noContent().build();
        }
        List<Chat> chats = chatService.getAllPrivateChats(currentUser);
        Chat existingChat = chats.stream().filter(c -> c.getUserList().contains(user)).findFirst().orElse(null);
        if (existingChat == null) {
            Chat newChat = new Chat(true);
            newChat.setUserList(Arrays.asList(currentUser, user));
            newChat.setToken(chatService.generateToken(currentUser, user));
            PhotoAlbum photoAlbum = new PhotoAlbum(currentUser.getNickName() + user.getNickName() + "Album");
            newChat.setPhotoAlbum(albumService.save(photoAlbum));
            chatService.createChat(newChat);
            return ResponseEntity.ok(newChat.getToken());
        } else {
            return ResponseEntity.ok(existingChat.getToken());
        }
    }


    @Operation(summary = "Get last messages", description = "limit 20 messages", tags = {"Private chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Message.class)))),
            @ApiResponse(responseCode = "204", description = "Page parameter is greater than the total number of pages")})
    @GetMapping(value = "/messages/{chatToken}")
    ResponseEntity<List<Message>> getLastMessages(@RequestParam(value = "page", required = false) Integer page,
                                                  @PathVariable String chatToken) {

        if (page == null) page = 0;
        Pageable pageable = PageRequest.of(page, 20, Sort.by("id").descending());
        Chat chat = chatService.getChatByToken(chatToken);
        Page<Message> msgPage = messageService.getPagebleMessages(chat, pageable);
        if (msgPage.getTotalPages() > page) {
            return ResponseEntity.ok(msgPage.getContent());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all photos from chat", tags = {"Private chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Photo.class)))),
            @ApiResponse(responseCode = "204", description = "Wrong chat token")})
    @GetMapping(value = "/photos/{chatToken}")
    ResponseEntity<List<Photo>> getChatPhotos(@PathVariable String chatToken) {
        Chat chat = chatService.getChatByToken(chatToken);
        if (chat == null) {
            return ResponseEntity.noContent().build();
        }
        PhotoAlbum album = chat.getPhotoAlbum();
        return ResponseEntity.ok(albumService.getAllPhotosByAlbum(album));
    }


    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get another user's info", description = "Avatar and username", tags = {"Private chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "204", description = "Wrong chat token")})
    @GetMapping(value = "/user/{chatToken}")
    ResponseEntity<Map<String, String>> getAnotherUserInfoByChatToken(@PathVariable String chatToken,
                                                                      @AuthenticationPrincipal User currentUser) {
        Chat chat = chatService.getChatByToken(chatToken);
        if (chat == null) {
            return ResponseEntity.noContent().build();
        }
        User user = chat.getUserList().stream().filter(u -> !u.equals(currentUser)).findFirst().get();
        Map<String, String> info = new HashMap<>();
        info.put("username", user.getNickName());
        info.put("ava", user.getAvatar().getSmall());
        return ResponseEntity.ok(info);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Check User", description = "Check in session registry if user is logged in", tags = {"Private Chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Boolean.class)))})
    @GetMapping(value = "/online/{id}")
    ResponseEntity<Boolean> isUserLoggedIn(@PathVariable Long id) {
        List<Long> loggedUsers = securityUtilsService.getUsersFromSessionRegistry();
        boolean isLogged = loggedUsers.contains(id);
        return ResponseEntity.ok(isLogged);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Check message date", description = "Check if first message date is > 6 month", tags = {"Private Chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Boolean.class)))})
    @GetMapping(value = "/message/{chatToken}")
    ResponseEntity<Boolean> checkMessageDate(@PathVariable String chatToken) {
        boolean result;
        Chat chat = chatService.getChatByToken(chatToken);
        Message msg = messageService.findFirstMessageByChat(chat);
        result = msg.getMessageDate().isBefore(LocalDateTime.now().minusMonths(6L));
        return ResponseEntity.ok(result);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete chat messages",
            description = "Delete all chat messages if param all=true, or else delete messages older than month",
            tags = {"Private Chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "204", description = "Wrong chat token")})
    @DeleteMapping(value = "/messages/{chatToken}")
    ResponseEntity<String> deleteMessages(@PathVariable String chatToken,
                                          @Parameter(description = "Delete all messages or messages that older than month", required = true)
                                          @RequestParam(value = "all") Boolean all) {
        Chat chat = chatService.getChatByToken(chatToken);
        if (chat == null) {
            return ResponseEntity.noContent().build();
        }
        messageService.deleteMessages(true, all, chatToken);
        albumService.deleteAlbumPhotos(all, chatService.getChatByToken(chatToken).getPhotoAlbum());
        return ResponseEntity.ok().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete private chat message",
            description = "Delete private chat message",
            tags = {"Private Chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "204", description = "Wrong chat token")})
    @DeleteMapping(value = "/message/{id}")
    ResponseEntity<String> deleteMessage(@Parameter(description = "Message id", required = true)
                                         @PathVariable Long id) {
        User user = securityUtilsService.getLoggedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean isModer = securityUtilsService.isModerator() || securityUtilsService.isAdmin();
        boolean isSender = messageService.findMessage(id).getSender().equals(user.getNickName());
        if (!isSender || !isModer) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        messageService.deleteMessage(id);
        return ResponseEntity.ok().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Upload image", tags = {"Private Chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Map originalImg:fileName, thumbnailImg:fileName",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "204", description = "Wrong chat token")})
    @PostMapping("/image/{chatToken}")
    ResponseEntity<Map<String, String>> processImage(@Parameter(description = "Image file", required = true)
                                                     @RequestParam("file-input") MultipartFile file,
                                                     @PathVariable(value = "chatToken") String chatToken) {
        Chat chat = chatService.getChatByToken(chatToken);
        if (chat == null) {
            return ResponseEntity.noContent().build();
        }
        Map<String, String> result = new HashMap<>();
        PhotoAlbum album = chat.getPhotoAlbum();
        Photo photo = photoService.save(album, file, 0);
        result.put("originalImg", photo.getOriginal());
        result.put("thumbnailImg", photo.getSmall());
        return ResponseEntity.ok(result);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Edit message", tags = {"Private Chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Message.class))),
            @ApiResponse(responseCode = "400", description = "Error editing message"),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @PutMapping(value = "/message/edit/{chatToken}", produces = {"application/json"})
    public ResponseEntity<Message> editMessage(@PathVariable("chatToken") String chatToken,
                                               @RequestBody Message chatMessage,
                                               @Value("${privateMessage.allowedEditingTime}") Long allowedTime) {

        Message message = messageService.findMessage(chatMessage.getId());
        User user = userService.getUserByNickName(message.getSender());
        User currentUser = securityUtilsService.getLoggedUser();
        long hours = message.getMessageDate().until(LocalDateTime.now(), ChronoUnit.HOURS);

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        message.setReplyTo(chatMessage.getReplyTo());
        message.setText(chatMessage.getText());
        message.setEditMessageDate(LocalDateTime.now());

        if (!chatToken.equals(message.getChat().getToken()) || hours > allowedTime || !currentUser.equals(user)) {
            return ResponseEntity.badRequest().build();
        }
        messageService.editMessage(message);
        return ResponseEntity.ok(message);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Read message", tags = {"Private Chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Message.class))),
            @ApiResponse(responseCode = "400", description = "Error reading message"),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @PutMapping(value = "/message/read/{id}", produces = {"application/json"})
    ResponseEntity<String> readMessage(@Parameter(description = "Message id", required = true)
                                       @PathVariable Long id) {

        Message message;
        try {
            message = messageService.findMessage(id);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        message.setRead(true);
        messageService.editMessage(message);
        return ResponseEntity.ok().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all private chats", description = "Get all private chats", tags = {"Private Chat"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Private chat list",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "204", description = "chat not found")})
    @GetMapping(value = "/allchats")
    public ResponseEntity<List<PrivateChatDto>> getAllChatByLoggedUser(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.noContent().build();
        }
        List<PrivateChatDto> dtos = new ArrayList<>();
        List<Chat> chats = chatService.getAllPrivateChats(currentUser);
        Collections.reverse(chats);
        HashMap<Long, Message> chatAndLAstMessage = messageService.getAllChatsLastMessage(chats);
        HashMap<Long, Integer> chatUnread = messageService.getChatIdAndUnreadMessage(chats);
        for (Chat chat : chats) {
            Message chatLastMessage = chatAndLAstMessage.get(chat.getId());
            User anotherUser = chat.getUserList().stream().filter(u -> !u.equals(currentUser)).findFirst().get();
            String ava = anotherUser.getAvatar().getSmall();
            Long id = chat.getId();
            String lastMessage = chatLastMessage.getText();
            String firstName = anotherUser.getFirstName();
            int unread = chatUnread.get(chat.getId());
            Long millis = chatLastMessage.getMessageDate().atZone(ZoneId.of("Europe/Moscow")).toInstant().toEpochMilli();
            dtos.add(new PrivateChatDto(id, lastMessage, unread, firstName, ava, millis));
        }
        return ResponseEntity.ok(dtos);
    }
}
