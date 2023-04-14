package ru.oldranger.club.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.oldranger.club.dto.UserStatisticDto;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.model.user.UserStatistic;

import java.util.List;

public interface UserStatisticService {

    UserStatistic getUserStaticById(Long id);

    UserStatistic getUserStaticByUser(User user);

    void saveUserStatic(UserStatistic userStatistic);

    Page<UserStatisticDto> getAllUserStatistic(Pageable pageable);

    Page<UserStatisticDto> getUserStatisticsByQuery(Pageable pageable, String query);

    List<UserStatisticDto> getUserStatisticDtoFromUserStatistic(List<UserStatistic> users);
}