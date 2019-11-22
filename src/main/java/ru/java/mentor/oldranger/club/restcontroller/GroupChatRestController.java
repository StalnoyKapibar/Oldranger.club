package ru.java.mentor.oldranger.club.restcontroller;


import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.chat.Message;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.chat.ChatService;
import ru.java.mentor.oldranger.club.service.chat.MessageService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/chat")
public class GroupChatRestController {

    ChatService chatService;
    MessageService messageService;

    @GetMapping("/user")
    ResponseEntity<Map<String, String>> getUserInfo(@AuthenticationPrincipal User user) {
        Map<String, String> info = new HashMap<>();
        info.put("username", user.getNickName());
        info.put("ava", user.getAvatar().getSmall());
        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    @GetMapping("/users")
    ResponseEntity<Map<String, Long>> getOnlineUsers() {
        List<User> userList = chatService.getChatById(1L).getUserList();
        Map<String, Long> users = new HashMap<>();
        for (User user : userList) {
            users.put(user.getNickName(), user.getId());
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/messages")
    ResponseEntity<List<Message>> getLastMessages(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(value = "page", required = false) Integer page) {

        Chat chat = chatService.getChatById(1L);
        if (page != null) {
            pageable = PageRequest.of(page, 20, Sort.by("id"));
        }
        Page<Message> msgPage = messageService.getPagebleMessages(chat,pageable);
        int pageCount = msgPage.getTotalPages();
        return new ResponseEntity<>(msgPage.getContent(), HttpStatus.OK);
    }

    @PostMapping("/image")
    ResponseEntity<Map<String,String>> processImage(@RequestParam("file-input") MultipartFile file) throws IOException {
        Map<String,String> result = messageService.processImage(file);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
