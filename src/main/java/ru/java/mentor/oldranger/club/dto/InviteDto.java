package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.java.mentor.oldranger.club.model.user.User;

@Getter
@Setter
@AllArgsConstructor
public class InviteDto {

    private User user;
    private String key;
}
