package ru.java.mentor.oldranger.club.service.utils.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.BlackListRepository;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BlackList;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.BlackListService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BlackListServiceImpl implements BlackListService {

    private static BlackListRepository blackListRepository;
    private static UserService userService;

    @Autowired
    BlackListServiceImpl(BlackListRepository blackListRepository, UserService userService) {
        this.blackListRepository = blackListRepository;
        this.userService = userService;
    }

    private static BlackListServiceImpl instance;

    private BlackListServiceImpl() {
    }

    public static BlackListServiceImpl getInstance() {
        if (instance == null) {
            instance = new BlackListServiceImpl(blackListRepository, userService);
        }
        return instance;
    }

    @Override
    public void createBlockForUser(Long id, LocalDateTime time) {
        User user = userService.findById(id);
        BlackList blackList = new BlackList(user, time);
        blackListRepository.save(blackList);
    }

    @Override
    public void deleteBlock(Long id) {
        blackListRepository.deleteById(id);
    }

    @Override
    public boolean userSearchBlackListByUserId(Long id) {
        List<BlackList> blackLists = findByUserId(id);
        if (blackLists.size() == 0) {
            return true;
        } else {
            deleteUnlockBlock(blackLists);
            return false;
        }
    }

    private void deleteUnlockBlock(List<BlackList> blackLists) {
        LocalDateTime now = LocalDateTime.now();
        for (BlackList blackList : blackLists) {
            if (blackList.getUnlockTime() == null) {
                break;
            } else if (now.isAfter(blackList.getUnlockTime())) {
                blackListRepository.delete(blackList);
            }
        }
    }

    @Override
    public List<BlackList> findAll() {
        return blackListRepository.findAll();
    }

    @Override
    public void save(BlackList blackList) {
        blackListRepository.save(blackList);
    }

    @Override
    public List<BlackList> findByUserId(Long id) {
        try {
            List<BlackList> blackList = blackListRepository.findByUserId(id);
            return blackList;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public BlackList findByUser(User user) {
        BlackList blackList = blackListRepository.findByUser(user);
        return blackList;
    }
}