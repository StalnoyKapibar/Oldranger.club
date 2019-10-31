package ru.java.mentor.oldranger.club.service.user;

import ru.java.mentor.oldranger.club.model.user.UserStatistic;

public interface UserStatisticService {

    public UserStatistic getUserStaticById(Long id);

    public void saveUserStatic(UserStatistic userStatistic);
}
