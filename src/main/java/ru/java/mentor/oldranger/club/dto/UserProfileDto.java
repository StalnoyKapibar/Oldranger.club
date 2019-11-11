package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.java.mentor.oldranger.club.model.user.Gender;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {

    private String nickName;
    private String firstName;
    private String lastName;
    private LocalDateTime birthday;
    private Gender gender;
    private String country;
    private String city;

    private String email;
    private String phoneNumber;
    private String socialVk;
    private String socialFb;
    private String socialTw;

    private LocalDateTime regDate;
    private long messageCount;
    private long topicStartCount;
    private LocalDateTime lastComment;
    private LocalDateTime lastVizit;

    private String aboutMe;

}
