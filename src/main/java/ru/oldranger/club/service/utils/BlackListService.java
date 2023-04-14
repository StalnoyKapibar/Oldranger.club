package ru.oldranger.club.service.utils;

import ru.oldranger.club.model.user.User;
import ru.oldranger.club.model.utils.BlackList;

import java.time.LocalDateTime;
import java.util.List;


public interface BlackListService {

    void createBlockForUser(Long id, LocalDateTime time);

    void deleteBlock(Long id);

    boolean userSearchBlackListByUserId(Long id);

    List<BlackList> findAll();

    void save(BlackList blackList);

    List<BlackList> findByUserId(Long id);

    BlackList findByUser(User user);
}
