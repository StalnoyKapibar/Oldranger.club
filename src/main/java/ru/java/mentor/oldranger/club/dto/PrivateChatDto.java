package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.java.mentor.oldranger.club.model.chat.Message;
import ru.java.mentor.oldranger.club.model.user.UserAvatar;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class PrivateChatDto {
    private Long id;
    private String lastMessage;
    private int unreadMessge;
    private String firstName;
    private UserAvatar avatar;
    private Long millis;
}

