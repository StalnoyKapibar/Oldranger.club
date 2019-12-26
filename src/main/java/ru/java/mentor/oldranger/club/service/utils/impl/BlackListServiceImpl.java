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
        LOG.debug("Getting blacklist for user with id = {}", id);
        List<BlackList> blackLists = findByUserId(id);
        if (blackLists.size() == 0 || deleteUnlockBlock(blackLists)) {
            return true;
        }
        return false;
    }

    private boolean deleteUnlockBlock(List<BlackList> blackLists) {
       LOG.debug("Checking ban unlock time");
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
        LOG.debug("Getting all blacklists");
        List<BlackList> blackList = new ArrayList<>();
        try {
            blackList = blackListRepository.findAll();
            LOG.debug("Returned list of {} elements", blackList.size());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return blackList;
    }

    @Override
    public void save(BlackList blackList) {
        LOG.info("Saving user blackList");
        try {
            BlackList saved = blackListRepository.save(blackList);
            LOG.info("User blackList {} saved", saved);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public List<BlackList> findByUserId(Long id) {
        LOG.debug("Getting blacklist for user with id = {}", id);
        List<BlackList> blackList = new ArrayList<>();
        try {
            blackList = blackListRepository.findByUserId(id);
            LOG.debug("Returned blacklist");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return blackList;
    }

    @Override
    public BlackList findByUser(User user) {
        LOG.debug("Getting blacklist for user {}", user);
        BlackList blackList = null;
        try {
            blackList = blackListRepository.findByUser(user);
            LOG.debug("Returned blacklist");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return blackList;
    }
}