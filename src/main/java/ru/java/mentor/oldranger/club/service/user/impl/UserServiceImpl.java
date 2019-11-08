package ru.java.mentor.oldranger.club.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.UserRepository;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserAvatar;
import ru.java.mentor.oldranger.club.service.user.UserService;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long theId) {
        Optional<User> result = userRepository.findById(theId);
        return result.orElseThrow(() -> new RuntimeException("Did not find user id - " + theId));
    }

    @Override
    public void save(User user) {
        if (user.getAvatar() == null) {
            user.setAvatar(setDefaultAvatar());
        }
        userRepository.save(user);
    }

    @Override
    public void deleteById(Long theId) {
        userRepository.deleteById(theId);
    }

    @Override
    public User getUserByNickName(String name) {
        return userRepository.findUserByNickName(name);
    }

    private UserAvatar setDefaultAvatar() {
        UserAvatar def = new UserAvatar();
        def.setOriginal("default.png");
        def.setMedium("default.png");
        def.setSmall("default-sm.png");
        return def;
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
}