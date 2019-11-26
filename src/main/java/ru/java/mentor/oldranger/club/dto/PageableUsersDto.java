package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PageableUsersDto {

    private Page<UserStatistic> users;
    private int pageCount;
    private List<UserStatistic> usersList;
    private String query;
}
