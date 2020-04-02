package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.dao.UserRepository.UserAvatarRepository;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserAvatar;
import ru.java.mentor.oldranger.club.service.user.UserAvatarService;
import ru.java.mentor.oldranger.club.service.user.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAvatarServiceImpl implements UserAvatarService {

    @Value("${upload.location:${user.home}}")
    private String uploadDir;
    @Value("${upload.medium}")
    private int medium;
    @Value("${upload.small}")
    private int small;


    @NonNull
    private UserAvatarRepository userAvatarRepository;
    @NonNull
    private UserService userService;

    @Override
    public void save(UserAvatar avatar) {
        log.info("Saving avatar {}", avatar);
        try {
            userAvatarRepository.save(avatar);
            log.info("Avatar saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public String uploadImage(MultipartFile file) {
        log.info("Uploading file {}", file.getOriginalFilename());
        File uploadPath = new File(uploadDir);
        String resultFileName = null;
        try {
            if (!uploadPath.exists()) {
                uploadPath.mkdir();
            }
            resultFileName = UUID.randomUUID().toString() + StringUtils.cleanPath(file.getOriginalFilename());
            Path copyLocation = Paths
                    .get(uploadDir + File.separator + resultFileName);
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("File uploaded");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return resultFileName;
    }

    public String thumbnailImage(String imageName, int size, MultipartFile file) {
        log.info("Thumbnailing image {} to size {}", file.getOriginalFilename(), size);
        String resultFileName = null;
        try {
            resultFileName = UUID.randomUUID().toString() + "__" +
                    size + "px" + StringUtils.cleanPath(file.getOriginalFilename());
            String resultFileLocation = Paths
                    .get(uploadDir + File.separator + resultFileName).toString();
            Thumbnails.of(uploadDir + File.separator + imageName)
                    .size(size, size)
                    .toFile(resultFileLocation);
            log.info("Image thumbnailed");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return resultFileName;
    }

    private UserAvatar createNewAvatar(MultipartFile file) {
        log.debug("Creating new user avatar");
        UserAvatar userAvatar = new UserAvatar();
        userAvatar.setOriginal(uploadImage(file));
        userAvatar.setMedium(thumbnailImage(userAvatar.getOriginal(), medium, file));
        userAvatar.setSmall(thumbnailImage(userAvatar.getOriginal(), small, file));
        return userAvatar;
    }

    public void setAvatarToUser(User user, MultipartFile file) {
        log.info("Setting avatar to user {}", user);
        try {
            UserAvatar userAvatar = createNewAvatar(file);
            save(userAvatar);
            user.setAvatar(userAvatar);
            userService.save(user);
            log.info("Avatar set");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void deleteUserAvatar(User user) {
        log.info("Deleting avatar of user {}", user);
        try {
            UserAvatar userAvatar = user.getAvatar();
            if (!userAvatar.getOriginal().equals("default.png")) {
                Files.deleteIfExists(Paths.get(uploadDir + File.separator + userAvatar.getOriginal()));
                Files.deleteIfExists(Paths.get(uploadDir + File.separator + userAvatar.getMedium()));
                Files.deleteIfExists(Paths.get(uploadDir + File.separator + userAvatar.getSmall()));
                user.setAvatar(null);
                userService.save(user);
                userAvatarRepository.delete(userAvatar);
            }
            log.info("Avatar deleted");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void updateUserAvatar(User user, MultipartFile file) {
        log.info("Updating user {} avatar", user);
        deleteUserAvatar(user);
        setAvatarToUser(user, file);
        log.info("Avatar updated");
    }
}