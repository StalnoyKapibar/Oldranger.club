package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProfileDto {

    private long userId;
    private String nickName;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime regDate;
    private long messageCount;
    private long topicStartCount;
    private LocalDateTime lastComment;
    private LocalDateTime lastVizit;
    private String avatar;
    private boolean owner;
    private boolean isUser;
    private String city;
    private String country;
    private String phoneNumber;
    private String socialVk;
    private String socialFb;
    private String socialTw;
    private String aboutMe;
}
