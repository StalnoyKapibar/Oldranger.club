package ru.java.mentor.oldranger.club.service.user;

import ru.java.mentor.oldranger.club.model.user.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    User findById(Long theId);

    void save(User user);

    void deleteById(Long theId);

    User getUserByNickName(String login);

    User getUserByEmail(String email);

    User getUserByEmailOrNickName(String login);

    User getUserByInviteKey(String key);
}