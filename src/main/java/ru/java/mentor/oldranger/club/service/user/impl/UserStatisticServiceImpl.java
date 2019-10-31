package ru.java.mentor.oldranger.club.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.UserStaticRepository;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

@Service
public class UserStatisticServiceImpl implements UserStatisticService {

    @Autowired
    private UserStaticRepository userStaticRepository;

    @Override
    public UserStatistic getUserStaticById(Long id) {
        return userStaticRepository.getOne(id);
    }

    @Override
    public void saveUserStatic(UserStatistic userStatistic) {
        userStaticRepository.save(userStatistic);
    }
}
