package ru.java.mentor.oldranger.club.service.utils.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.BlackListRepository;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BlackList;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.BlackListService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BlackListServiceImpl implements BlackListService {

    private static BlackListRepository blackListRepository;
    private static UserService userService;
    private static BlackListServiceImpl instance;

    @Autowired
    BlackListServiceImpl(BlackListRepository blackListRepository, UserService userService) {
        this.blackListRepository = blackListRepository;
        this.userService = userService;
    }

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
        log.info("Banning user with id = {} for {}", id, time);
        try {
            User user = userService.findById(id);
            BlackList blackList = new BlackList(user, time);
            blackListRepository.save(blackList);
            log.debug("User banned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void deleteBlock(Long id) {
        log.info("Deleting user ban with id = {}", id);
        try {
            blackListRepository.deleteById(id);
            log.debug("User ban deleted");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public boolean userSearchBlackListByUserId(Long id) {
        log.debug("Getting blacklist for user with id = {}", id);
        List<BlackList> blackLists = findByUserId(id);
        if (blackLists.size() == 0 || deleteUnlockBlock(blackLists)) {
            return true;
        }
        return false;
    }

    //clear cache
    private boolean deleteUnlockBlock(List<BlackList> blackLists) {
        log.debug("Checking ban unlock time");
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
    //add caching
    public List<BlackList> findAll() {
        log.debug("Getting all blacklists");
        List<BlackList> blackList = new ArrayList<>();
        try {
            blackList = blackListRepository.findAll();
            log.debug("Returned list of {} elements", blackList.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return blackList;
    }

    @Override
    //add clear cache
    public void save(BlackList blackList) {
        log.info("Saving user blackList");
        try {
            BlackList saved = blackListRepository.save(blackList);
            log.info("User blackList {} saved", saved);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    //add caching
    public List<BlackList> findByUserId(Long id) {
        log.debug("Getting blacklist for user with id = {}", id);
        List<BlackList> blackList = new ArrayList<>();
        try {
            blackList = blackListRepository.findByUserId(id);
            log.debug("Returned blacklist");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return blackList;
    }

    @Override
    //add caching
    public BlackList findByUser(User user) {
        log.debug("Getting blacklist for user {}", user);
        BlackList blackList = null;
        try {
            blackList = blackListRepository.findByUser(user);
            log.debug("Returned blacklist");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return blackList;
    }

    @Override
    public Page<BlackList> getAllBlockedUsers(Pageable pageable) {
        log.debug("Getting page {} of user statistic", pageable.getPageNumber());
        Page<BlackList> page = null;
        try {
            page = blackListRepository.findAll(pageable);
            log.debug("Page returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return page;
    }
}