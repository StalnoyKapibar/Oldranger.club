package ru.java.mentor.oldranger.club.controller;

import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.java.mentor.oldranger.club.model.chat.Message;
import ru.java.mentor.oldranger.club.service.chat.ChatService;
import ru.java.mentor.oldranger.club.service.chat.MessageService;

import java.time.LocalDateTime;

@Controller
@AllArgsConstructor
public class PrivateChatController {

    private ChatService chatService;
    private MessageService messageService;


    @GetMapping("/private/{id}")
    public String getChatByUser(){
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
    public Message addUser(@Payload Message chatMessage) { return chatMessage; }

    @MessageMapping("/del/{chatToken}")
    @SendTo("/channel/private/{chatToken}")
    public Message delUser(@Payload Message chatMessage) { return chatMessage; }

}
