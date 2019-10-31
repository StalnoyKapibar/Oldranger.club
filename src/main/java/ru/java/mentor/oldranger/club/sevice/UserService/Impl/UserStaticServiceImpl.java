package ru.java.mentor.oldranger.club.sevice.UserService.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.UserStaticRepository;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.sevice.UserService.UserStaticService;

@Service
public class UserStaticServiceImpl implements UserStaticService {

    @Autowired
    private UserStaticRepository userStaticRepository;

    @Override
    public UserStatistic getUserStaticById(Long id) {
        return null;
    }

    @Override
    public void saveUserStatic(UserStatistic userStatistic) {

    }
}
