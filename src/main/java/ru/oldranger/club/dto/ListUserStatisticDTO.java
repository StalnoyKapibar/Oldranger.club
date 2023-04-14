package ru.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListUserStatisticDTO {

    private List<UserStatisticDto> users;
    private Long usersCount;
}
