package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.java.mentor.oldranger.club.dao.UserRepository.UserRepository;
import ru.java.mentor.oldranger.club.dto.UpdateProfileDto;
import ru.java.mentor.oldranger.club.dto.UserAuthDTO;
import ru.java.mentor.oldranger.club.model.media.Media;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserAvatar;
import ru.java.mentor.oldranger.club.model.user.UserProfile;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.media.MediaService;
import ru.java.mentor.oldranger.club.service.user.UserProfileService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
@RequiredArgsConstructor
@CacheConfig(cacheNames = "user", cacheManager = "generalCacheManager")
public class UserServiceImpl implements UserService {

    @Value("${upload.location:${user.home}}")
    private String uploadDir;
    @Value("${upload.medium}")
    private int medium;
    @Value("${upload.small}")
    private int small;

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
    @Cacheable
    public User findById(Long theId) {
        log.debug("Getting user with id = {}", theId);
        Optional<User> result = userRepository.findById(theId);
        return result.orElseThrow(() -> new RuntimeException("Did not find user id - " + theId));
    }

    @Override
    @Caching(put = {
                    @CachePut(key = "#user.id", condition = "#user.id!=null"),
                    @CachePut(key = "#user.nickName", condition = "#user.id!=null&&#user.nickName!=null"),
                    @CachePut(key = "#user.email", condition = "#user.id!=null&&#user.email!=null"),
                    @CachePut(key = "#user.invite", condition = "#user.id!=null&&#user.invite!=null")})
    public User save(User user) {
        log.info("Saving user");
        User savedUser = null;
        try {
            savedUser = userRepository.save(user);
            Long id = savedUser.getId();
            if (user.getAvatar() == null) {
                UserAvatar defaultAvatar = setDefaultAvatar(id);
                savedUser.setAvatar(defaultAvatar);
                userRepository.save(savedUser);
            }

            if (userStatistic.getUserStaticByUser(user) == null) {
                userStatistic.saveUserStatic(new UserStatistic(user));
            }
            if (userProfileService.getUserProfileByUser(user) == null) {
                userProfileService.createUserProfile(new UserProfile(user));
            }
            if (mediaService.findMediaByUser(user) == null) {
                Media media = new Media();
                media.setUser(user);
                mediaService.save(media);
            }
            log.info("User {} saved", savedUser);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return savedUser;
    }


    @Override
    @Caching(put = {
                    @CachePut(key = "#user.id"),
                    @CachePut(key = "#user.nickName"),
                    @CachePut(key = "#user.email"),
                    @CachePut(key = "#user.invite")})
    public User updateUser(User user, UpdateProfileDto updateProfileDto) {
        log.info("Updating user {}", user);
        user.setNickName(updateProfileDto.getNickName());
        user.setFirstName(updateProfileDto.getFirstName());
        user.setLastName(updateProfileDto.getLastName());

        User savedUser = null;
        try {
            savedUser = userRepository.save(user);
            log.info("User {} updated", user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return savedUser;
    }

    @Override
    @CacheEvict(allEntries = true)
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
    @Cacheable
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

    @Override
    @Cacheable
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
    @Cacheable
    public User getUserByEmailOrNickName(String login) {
        log.debug("Getting user with email or nickname = {}", login);
        Optional<User> result = userRepository.findUserByEmailOrNickName(login);
        return result.orElseThrow(() -> new UsernameNotFoundException("There is no such user."));
    }

    @Override
    @Cacheable
    public User getUserByInviteKey(String key) {
        log.debug("Get user by invite key ={}", key);
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
                LocalDateTime.now(),
                currentUser);
    }


    @Override
    public Long getCount() {
        log.debug("Count users");
        return userRepository.count();
    }

    public UserAvatar setDefaultAvatar(Long id) {
        File uploadPath = new File(uploadDir);
        Path avatarPath = Paths.get(uploadDir + File.separator + "avatarForUserWithID_" + id + ".jpg");
        String resultFileName = small + "px" + StringUtils.cleanPath(avatarPath.toFile().getName());
        String resultFileLocation = Paths
                .get(uploadDir + File.separator + resultFileName).toString();
        try {
            if (!uploadPath.exists()) {
                uploadPath.mkdir();
            }
            Path path = Paths.get(uploadDir + File.separator + "default.png");
            Files.copy(path, avatarPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File uploaded");

            Thumbnails.of(avatarPath.toString())
                    .size(small, small)
                    .toFile(resultFileLocation);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        UserAvatar defaultAvatar = new UserAvatar();
        defaultAvatar.setIsDefaultAvatar(true);
        defaultAvatar.setOriginal(avatarPath.toString());
        defaultAvatar.setMedium(avatarPath.toString());
        defaultAvatar.setMedium(resultFileLocation);
        return defaultAvatar;
    }

}