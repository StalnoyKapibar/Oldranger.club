package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;

import java.util.List;

@Data
@AllArgsConstructor
public class ListUserStatisticDTO {

    private List<UserStatisticDto> users;
    private Integer usersCount;
}
