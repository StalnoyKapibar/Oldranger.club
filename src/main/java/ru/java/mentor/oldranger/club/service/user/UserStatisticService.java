package ru.java.mentor.oldranger.club.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.java.mentor.oldranger.club.dto.UserStatisticDto;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;

import java.util.List;

public interface UserStatisticService {

    UserStatistic getUserStaticById(Long id);

    UserStatistic getUserStaticByUser(User user);

    void saveUserStatic(UserStatistic userStatistic);

    Page<UserStatistic> getAllUserStatistic(Pageable pageable);

    Page<UserStatistic> getUserStatisticsByQuery(Pageable pageable, String query);

    List<UserStatisticDto> getUserStatisticDtoFromUserStatistic(List<UserStatistic> users);
}