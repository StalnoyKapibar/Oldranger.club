package ru.java.mentor.oldranger.club.service.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BlackList;

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

    Page<BlackList> getAllBlockedUsers(Pageable pageable);
}
