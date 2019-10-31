package ru.java.mentor.oldranger.club.service.user;

import ru.java.mentor.oldranger.club.model.user.User;

import java.util.List;

public interface UserService {

    public List<User> findAll();

    public User findById(Long theId);

    public void save(User user);

    public void deleteById(Long theId);

    User getUserByNickName(String login);
}