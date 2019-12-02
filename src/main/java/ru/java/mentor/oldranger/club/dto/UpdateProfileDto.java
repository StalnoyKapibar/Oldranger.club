package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.java.mentor.oldranger.club.model.user.UserProfile;

@Getter
@Setter
@AllArgsConstructor
public class UpdateProfileDto {

    private UserProfile profile;
    private ErrorDto error;
}
