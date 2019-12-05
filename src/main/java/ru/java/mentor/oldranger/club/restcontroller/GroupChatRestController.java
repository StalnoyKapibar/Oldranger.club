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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.chat.Message;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.chat.ChatService;
import ru.java.mentor.oldranger.club.service.chat.MessageService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/chat")
@Tag(name = "Group chat")
public class GroupChatRestController {

    private ChatService chatService;
    private MessageService messageService;
    private SecurityUtilsService securityUtilsService;

    @Operation(security = @SecurityRequirement(name = "security"),
               summary = "Get current user info", description = "Avatar and username", tags = { "Group chat" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "204", description = "User is not logged in")})
    @GetMapping(value = "/user", produces = { "application/json" })
    ResponseEntity<Map<String, String>> getUserInfo() {
        User user = securityUtilsService.getLoggedUser();
        if (user == null) return ResponseEntity.noContent().build();

        Map<String, String> info = new HashMap<>();
        info.put("username", user.getNickName());
        info.put("ava", user.getAvatar().getSmall());
        return ResponseEntity.ok(info);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
               summary = "Get online users", tags = { "Group chat" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Map userNickname:userId",
                    content = @Content(schema = @Schema(implementation = Map.class)))})
    @GetMapping(value = "/users", produces = { "application/json" })
    ResponseEntity<Map<String, Long>> getOnlineUsers() {
        return ResponseEntity.ok(messageService.getOnlineUsers());
    }

    @Operation(summary = "Get last messages", description = "limit 20 messages", tags = { "Group chat" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Message.class)))),
            @ApiResponse(responseCode = "204", description = "Page parameter is greater than the total number of pages")})
    @GetMapping(value = "/messages", produces = { "application/json" })
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
               summary = "Upload image", tags = { "Group chat" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Map originalImg:fileName, thumbnailImg:fileName",
                    content = @Content(schema = @Schema(implementation = Map.class)))})
    @PostMapping("/image")
    ResponseEntity<Map<String,String>> processImage(@Parameter(description="Image file", required=true)
                                                    @RequestParam("file-input") MultipartFile file) throws IOException {
        Map<String,String> result = messageService.processImage(file);
        return ResponseEntity.ok(result);
    }

}
