package ru.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.oldranger.club.model.user.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProfileDto {

    private long userId;
    private String nickName;
    private String firstName;
    private String lastName;
    private String email;
    private String city;
    private String country;
    private LocalDate birthday;
    private Gender gender;
    private String phoneNumber;
    private String socialFb;
    private String socialTw;
    private String socialVk;
    private String aboutMe;
    private LocalDateTime regDate;
    private long messageCount;
    private long topicStartCount;
    private LocalDateTime lastComment;
    private LocalDateTime lastVisit;
    private String avatar;
    private boolean owner;
    private boolean isUser;

}
