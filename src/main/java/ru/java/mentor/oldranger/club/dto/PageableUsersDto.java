package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;

import java.util.List;

@Data
@AllArgsConstructor
public class PageableUsersDto {

    private Page<UserStatistic> users;
    private int pageCount;
    private List<UserStatistic> usersList;
    private String query;
}
