package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.java.mentor.oldranger.club.model.user.User;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersTreeDto {

    private String nickName;
    private Long inviteKey;
    private Long userParentId;
    private Long userId;

    public UsersTreeDto(String nickName, Long userId) {
        this.nickName = nickName;
        this.userId = userId;
    }
}
