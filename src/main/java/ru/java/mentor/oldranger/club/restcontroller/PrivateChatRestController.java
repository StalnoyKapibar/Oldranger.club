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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.chat.Message;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.chat.ChatService;
import ru.java.mentor.oldranger.club.service.chat.MessageService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/private")
public class PrivateChatRestController {

    private ChatService chatService;
    private MessageService messageService;
    private UserService userService;
    private SecurityUtilsService securityUtilsService;

    @Operation(security = @SecurityRequirement(name = "security"),
    summary = "Get chat token", description = "Get chat token by user id", tags = { "Private Chat" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat token",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "204", description = "User not found")})
    @GetMapping(value = "/{id}")
    public ResponseEntity<String> getChatIdByUser(@PathVariable Long id,
                                                @AuthenticationPrincipal User currentUser){
        User user = userService.findById(id);
        if (user == null || currentUser == null) {
            return ResponseEntity.noContent().build();
        }
        List<Chat> chats = chatService.getAllPrivateChats(currentUser);
        Chat existingChat = chats.stream().filter(c -> c.getUserList().contains(user)).findFirst().orElse(null);
        if (existingChat == null) {
            Chat newChat = new Chat(true);
            newChat.setUserList(Arrays.asList(currentUser,user));
            newChat.setToken(chatService.generateToken(currentUser,user));
            chatService.createChat(newChat);
            return ResponseEntity.ok(newChat.getToken());
        } else {
            return ResponseEntity.ok(existingChat.getToken());
        }
    }


    @Operation(summary = "Get last messages", description = "limit 20 messages", tags = { "Private chat" })
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
            summary = "Get another user's info", description = "Avatar and username", tags = { "Private chat" })
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
            summary = "Check User", description = "Check in session registry if user is logged in", tags = { "Private Chat" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Boolean.class)))})
    @GetMapping(value = "/online/{id}")
    ResponseEntity<Boolean> isUserLoggedIn(@PathVariable Long id){
        List<Long> loggedUsers = securityUtilsService.getUsersFromSessionRegistry();
        boolean isLogged = loggedUsers.contains(id);
        return ResponseEntity.ok(isLogged);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Check message date", description = "Check if first message date is > 6 month", tags = { "Private Chat" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Boolean.class)))})
    @GetMapping(value = "/message/{chatToken}")
    ResponseEntity<Boolean> checkMessageDate(@PathVariable String chatToken){
        boolean result;
        Chat chat = chatService.getChatByToken(chatToken);
        Message msg = messageService.findFirstMessageByChat(chat);
        result = msg.getMessageDate().isBefore(LocalDateTime.now().minusMonths(6L));
        return ResponseEntity.ok(result);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete chat messages",
            description = "Delete all chat messages if param all=true, or else delete messages older than month",
            tags = { "Private Chat" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = String.class)))})
    @DeleteMapping(value = "/messages/{chatToken}")
    ResponseEntity<String> deleteMessages(@PathVariable String chatToken,
                                          @Parameter(description="Delete all messages or messages that older than month", required=true)
                                          @RequestParam(value = "all") Boolean all){
        messageService.deleteMessages(true, all, chatToken);
        return ResponseEntity.ok().build();
    }
}
