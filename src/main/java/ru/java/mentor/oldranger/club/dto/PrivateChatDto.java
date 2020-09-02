package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PrivateChatDto {
    private Long id;
    private String lastMessage;
    private int unreadMessge;
    private String firstName;
    private String avatar;
    private Long millis;
}

