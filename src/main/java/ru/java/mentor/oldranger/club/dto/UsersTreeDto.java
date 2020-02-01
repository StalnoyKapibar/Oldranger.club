package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersTreeDto {

    private Long userParentId;
    private String nickName;
    private Long userId;
    private Long deepTree;
}
