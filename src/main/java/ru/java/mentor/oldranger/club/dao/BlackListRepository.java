package ru.java.mentor.oldranger.club.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BlackList;

import java.util.List;

public interface BlackListRepository extends JpaRepository<BlackList, Long> {

    List<BlackList> findByUserId(Long id);

    BlackList findByUser(User user);
}