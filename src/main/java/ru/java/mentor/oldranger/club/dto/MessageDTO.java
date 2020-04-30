package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.chat.Message;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private Long messageId;
    private String text;
    private String originalImg;
    private String thumbnailImg;
    private String fileName;
    private String filePath;
    private Message.MessageType type;
    private String sender;
    private String senderAvatar;
    private String replyTo;
    private Chat chat;
    private LocalDateTime messageDate;
    private LocalDateTime editMessageDate;
}
