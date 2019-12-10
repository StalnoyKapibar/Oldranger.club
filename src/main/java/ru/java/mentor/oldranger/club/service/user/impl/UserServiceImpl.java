package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.UserRepository;
import ru.java.mentor.oldranger.club.model.media.Media;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserAvatar;
import ru.java.mentor.oldranger.club.model.user.UserProfile;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.media.MediaService;
import ru.java.mentor.oldranger.club.service.user.UserProfileService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private UserProfileService userProfileService;
    private UserStatisticService userStatistic;
    private MediaService mediaService;

    @Autowired
    void setMediaService(MediaService service) {
        this.mediaService = service;
    }

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

        User savedUser = userRepository.save(user);

        if (userStatistic.getUserStaticByUser(user) == null) {
            userStatistic.saveUserStatic(new UserStatistic(user));
        }

        if (userProfileService.getUserProfileByUser(user) == null) {
            userProfileService.createUserProfile(new UserProfile(user));
        }

        Media media = new Media();
        media.setUser(user);
        mediaService.save(media);

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

    @Override
    public Optional<User> getUserByEmailOrNickName(String login) {
        return Optional.of(userRepository.findUserByEmailOrNickName(login));
    }
}