package ru.oldranger.club.controller;

import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.oldranger.club.model.chat.Message;
import ru.oldranger.club.service.chat.ChatService;
import ru.oldranger.club.service.chat.MessageService;
import ru.oldranger.club.service.media.PhotoService;

import java.time.LocalDateTime;

@Controller
@AllArgsConstructor
public class PrivateChatController {

    private ChatService chatService;
    private MessageService messageService;
    private PhotoService photoService;


    @GetMapping("/private/{id}")
    public String getChatByUser() {
        return "chat/privateChat";
    }

    @MessageMapping("/send/{chatToken}")
    @SendTo("/channel/private/{chatToken}")
    public Message sendMessage(@Payload Message chatMessage, @DestinationVariable String chatToken) {
        chatMessage.setMessageDate(LocalDateTime.now());
        chatMessage.setChat(chatService.getChatByToken(chatToken));
        messageService.addMessage(chatMessage);
        return chatMessage;
    }

    @MessageMapping("/add/{chatToken}")
    @SendTo("/channel/private/{chatToken}")
    public Message addUser(@Payload Message chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/del/{chatToken}")
    @SendTo("/channel/private/{chatToken}")
    public Message delUser(@Payload Message chatMessage) {
        return chatMessage;
    }

}
