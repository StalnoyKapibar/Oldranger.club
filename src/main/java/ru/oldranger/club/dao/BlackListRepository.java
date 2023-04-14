package ru.oldranger.club.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.model.utils.BlackList;

import java.util.List;

public interface BlackListRepository extends JpaRepository<BlackList, Long> {

    List<BlackList> findByUserId(Long id);

    BlackList findByUser(User user);
}