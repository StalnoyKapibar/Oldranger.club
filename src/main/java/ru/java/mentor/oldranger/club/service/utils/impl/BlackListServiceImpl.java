package ru.java.mentor.oldranger.club.service.utils.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(BlackListServiceImpl.class);
    private static BlackListRepository blackListRepository;
    private static UserService userService;

    @Autowired
    BlackListServiceImpl(BlackListRepository blackListRepository, UserService userService) {
        this.blackListRepository = blackListRepository;
        this.userService = userService;
    }

    private static BlackListServiceImpl instance;

    private BlackListServiceImpl() {}

    public static BlackListServiceImpl getInstance() {
        if (instance == null) {
            instance = new BlackListServiceImpl(blackListRepository, userService);
        }
        return instance;
    }

    @Override
    public void createBlockForUser(Long id, LocalDateTime time) {
        LOG.info("Banning user with id = {} for {}", id, time);
        try {
            User user = userService.findById(id);
            BlackList blackList = new BlackList(user, time);
            blackListRepository.save(blackList);
            LOG.debug("User banned");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void deleteBlock(Long id) {
        LOG.info("Deleting user ban with id = {}", id);
        try {
            blackListRepository.deleteById(id);
            LOG.debug("User ban deleted");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public boolean userSearchBlackListByUserId(Long id) {
        List<BlackList> blackLists = findByUserId(id);
        if (blackLists.size() == 0 || deleteUnlockBlock(blackLists)) {
            return true;
        }
        return false;
    }

    private boolean deleteUnlockBlock(List<BlackList> blackLists) {
        LocalDateTime now = LocalDateTime.now();
        for (BlackList blackList : blackLists) {
            if (blackList.getUnlockTime() == null) {
                return false;
            } else if (now.isAfter(blackList.getUnlockTime())) {
                blackListRepository.delete(blackList);
                return true;
            }
        }
        return false;
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