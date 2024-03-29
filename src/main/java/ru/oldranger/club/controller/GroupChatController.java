package ru.oldranger.club.controller;

import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.oldranger.club.model.chat.Chat;
import ru.oldranger.club.model.chat.Message;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.service.chat.ChatService;
import ru.oldranger.club.service.chat.MessageService;
import ru.oldranger.club.service.media.PhotoService;
import ru.oldranger.club.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@AllArgsConstructor
public class GroupChatController {

    private ChatService chatService;
    private MessageService messageService;
    private UserService userService;
    private PhotoService photoService;

    @GetMapping("/chat")
    public String getChatPage() {
        return "chat/chat";
    }

    @MessageMapping("/sendMessage")
    @SendTo("/channel/public")
    public Message sendMessage(@Payload Message chatMessage) {
        chatMessage.setMessageDate(LocalDateTime.now());
        chatMessage.setChat(chatService.getGroupChat());
        messageService.addMessage(chatMessage);
        return chatMessage;
    }

    @MessageMapping("/addUser")
    @SendTo("/channel/public")
    public Message addUser(@Payload Message chatMessage) {
        updateUserList(chatMessage, true);
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
