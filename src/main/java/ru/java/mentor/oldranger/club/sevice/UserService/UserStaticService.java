package ru.java.mentor.oldranger.club.sevice.UserService;

import ru.java.mentor.oldranger.club.model.user.UserStatistic;

public interface UserStaticService {

    public UserStatistic getUserStaticById(Long id);

    public void saveUserStatic(UserStatistic userStatistic);
}
