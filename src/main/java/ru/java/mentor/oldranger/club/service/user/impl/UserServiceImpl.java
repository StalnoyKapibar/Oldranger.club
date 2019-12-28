package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private UserProfileService userProfileService;
    private UserStatisticService userStatistic;
    private MediaService mediaService;

    @Override
    public List<User> findAll() {
        log.debug("Getting all users");
        List<User> users = null;
        try {
            users = userRepository.findAll();
            log.debug("Returned list of {} users", users.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return users;
    }

    @Override
    public User findById(Long theId) {
        log.debug("Getting user with id = {}", theId);
        Optional<User> result = userRepository.findById(theId);
        return result.orElseThrow(() -> new RuntimeException("Did not find user id - " + theId));
    }

    @Override
    public void save(User user) {
        log.info("Saving user");
        try {
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
            log.info("User {} saved", savedUser);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Long theId) {
        log.info("Deleting user with id = {}", theId);
        try {
            userRepository.deleteById(theId);
            log.info("User deleted");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public User getUserByNickName(String name) {
        log.debug("Getting user with nickname = {}", name);
        User user = null;
        try {
            user = userRepository.findUserByNickName(name);
            log.debug("User returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return user;
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
        log.debug("Getting user with email = {}", email);
        User user = null;
        try {
            user = userRepository.findUserByEmail(email);
            log.debug("User returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return user;
    }

    @Override
    public User getUserByEmailOrNickName(String login) {
        log.debug("Getting user with email or nickname = {}", login);
        Optional<User> result = userRepository.findUserByEmailOrNickName(login);
        return result.orElseThrow(() -> new UsernameNotFoundException("There is no such user."));
    }
}