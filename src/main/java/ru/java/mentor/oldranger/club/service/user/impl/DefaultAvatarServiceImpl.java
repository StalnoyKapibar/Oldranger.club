package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.java.mentor.oldranger.club.model.user.UserAvatar;
import ru.java.mentor.oldranger.club.service.user.DefaultAvatarService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultAvatarServiceImpl implements DefaultAvatarService {
    @Value("${upload.location:${user.home}}")
    private String uploadDir;
    @Value("${upload.medium}")
    private int medium;
    @Value("${upload.small}")
    private int small;

    @Override
    public UserAvatar setDefaultAvatar(Long id) {
        File uploadPath = new File(uploadDir);
        Path path = Paths.get(uploadDir + File.separator + "default.png");
        Path avatarPath = Paths.get(uploadDir + File.separator + "avatarForUserWithID_" + id + ".png");
        try {
            if (!uploadPath.exists()) {
                uploadPath.mkdir();
            }
            Files.copy(path, avatarPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File uploaded");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        UserAvatar defaultAvatar = new UserAvatar();
        defaultAvatar.setIsDefaultAvatar(true);
        defaultAvatar.setOriginal(avatarPath.getFileName().toString());
        defaultAvatar.setMedium(thumbnailImage(avatarPath, medium));
        defaultAvatar.setSmall(thumbnailImage(avatarPath, small));
        return defaultAvatar;
    }

    private String thumbnailImage(Path path, int size) {
        log.info("Thumbnailing image to size {}", size);
        String resultFileName = null;
        try {
            resultFileName = size + "px" + StringUtils.cleanPath(path.toFile().getName());
            String resultFileLocation = Paths
                    .get(uploadDir + File.separator + resultFileName).toString();
            Thumbnails.of(path.toString())
                    .size(size, size)
                    .toFile(resultFileLocation);
            log.info("Image thumbnailed");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return resultFileName;
    }
}
