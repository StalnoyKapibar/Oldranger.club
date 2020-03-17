package ru.java.mentor.oldranger.club.controller;

import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.chat.Message;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.chat.ChatService;
import ru.java.mentor.oldranger.club.service.chat.MessageService;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;


import java.time.LocalDateTime;
import java.util.List;

@Controller
@AllArgsConstructor
public class GroupChatController {

    private ChatService chatService;
    private MessageService messageService;
    private UserService userService;
    private PhotoService photoService;
    private SecurityUtilsService securityUtilsService;

    @GetMapping("/chat")
    public String getChatPage() {
        return "chat/chat";
    }

    @MessageMapping("/sendMessage")
    @SendTo("/channel/public")
    public Message sendMessage(@Payload Message chatMessage) {
        User user = securityUtilsService.getLoggedUser();
        chatMessage.setSenderAvatar(user.getAvatar().getSmall());
        chatMessage.setMessageDate(LocalDateTime.now());
        chatMessage.setChat(chatService.getGroupChat());
        messageService.addMessage(chatMessage);
        System.out.println(chatMessage.getId());
        return chatMessage;
    }

    @MessageMapping("/addUser")
    @SendTo("/channel/public")
    public Message addUser(@Payload Message chatMessage) {
        User user = securityUtilsService.getLoggedUser();
        chatMessage.setSenderAvatar(user.getAvatar().getSmall());
        updateUserList(chatMessage, true);
        System.out.println(chatMessage.getId());
        return chatMessage;
    }

    @MessageMapping("/delUser")
    @SendTo("/channel/public")
    public Message delUser(@Payload Message chatMessage) {
        updateUserList(chatMessage, false);
        return chatMessage;
    }

    private void updateUserList(Message chatMessage, boolean bool) {
        Chat chat = chatService.getGroupChat();
        User user = userService.getUserByNickName(chatMessage.getSender());
        List<User> users = chat.getUserList();

        if (bool) {
            users.add(user);
        } else {
            users.remove(user);
        }
        chat.setUserList(users);
        chatService.createChat(chat);
    }
}
