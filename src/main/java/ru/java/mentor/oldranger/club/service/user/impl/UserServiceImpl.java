package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.Select;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.RoleRepository;
import ru.java.mentor.oldranger.club.dao.UserRepository.UserRepository;
import ru.java.mentor.oldranger.club.dto.ProfileDto;
import ru.java.mentor.oldranger.club.dto.UserAuthDTO;
import ru.java.mentor.oldranger.club.model.media.Media;
import ru.java.mentor.oldranger.club.model.user.*;
import ru.java.mentor.oldranger.club.service.media.MediaService;
import ru.java.mentor.oldranger.club.service.user.UserProfileService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Slf4j
@Service
@AllArgsConstructor
@CacheConfig(cacheNames = {"users"})
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private UserProfileService userProfileService;
    private UserStatisticService userStatistic;
    private MediaService mediaService;
    private RoleRepository roleRepository;

    @Override
    @Cacheable(cacheNames = {"allUsers"})
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
    @Cacheable(key = "#theId")
    public User findById(Long theId) {
        log.debug("Getting user with id = {}", theId);
        Optional<User> result = userRepository.findById(theId);
        return result.orElseThrow(() -> new RuntimeException("Did not find user id - " + theId));
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "users", allEntries = true), @CacheEvict(value = "allUsers", allEntries = true)})
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
    @Caching(evict = {@CacheEvict(value = "users", key = "#theId"), @CacheEvict(value = "allUsers", allEntries = true)})
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
    @Cacheable(key = "#name")
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
    @Cacheable(key = "#email")
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
    @Cacheable(key = "#login")
    public User getUserByEmailOrNickName(String login) {
        log.debug("Getting user with email or nickname = {}", login);
        Optional<User> result = userRepository.findUserByEmailOrNickName(login);
        return result.orElseThrow(() -> new UsernameNotFoundException("There is no such user."));
    }

    @Override
    public User getUserByInviteKey(String key) {
        log.debug("Get user by inviti key ={}", key);
        return userRepository.findUserByInviteKey(key).orElseThrow(() -> new UsernameNotFoundException("There is no such invite key"));
    }

    @Override
    public UserAuthDTO buildUserDtoByUser(User user, boolean currentUser) {
        log.debug("Building current user dto");
        return new UserAuthDTO(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getNickName(),
                user.getRole().getRole(),
                user.getPassword(),
                LocalDateTime.now(),
                currentUser);
    }

}