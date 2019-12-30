package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UpdateProfileDto {

    private String nickName;
    private String firstName;
    private String lastName;
    private String email;
    private String city;
    private String country;
    private String phoneNumber;
    private String socialVk;
    private String socialFb;
    private String socialTw;
    private String aboutMe;
}
