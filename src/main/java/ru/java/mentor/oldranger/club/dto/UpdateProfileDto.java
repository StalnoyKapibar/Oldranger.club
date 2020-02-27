package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.java.mentor.oldranger.club.model.user.Gender;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileDto {

    private String firstName;
    private String lastName;
    private String email;
    private String nickName;
    private String city;
    private String country;
    private LocalDate birthday;
    private Gender gender;
    private String phoneNumber;
    private String socialFb;
    private String socialTw;
    private String socialVk;
    private String aboutMe;

}
