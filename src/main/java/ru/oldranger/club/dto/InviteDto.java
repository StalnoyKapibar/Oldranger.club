package ru.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.oldranger.club.model.user.User;

@Data
@AllArgsConstructor
public class InviteDto {

    private User user;
    private String key;
}
