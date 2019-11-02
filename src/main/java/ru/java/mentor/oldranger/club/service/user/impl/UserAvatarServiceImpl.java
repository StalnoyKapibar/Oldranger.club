package ru.java.mentor.oldranger.club.service.user.impl;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.dao.UserRepository.UserAvatarRepository;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserAvatar;
import ru.java.mentor.oldranger.club.service.user.UserAvatarService;
import ru.java.mentor.oldranger.club.service.user.UserService;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class UserAvatarServiceImpl implements UserAvatarService {

    @Value("${upload.location:${user.home}}")
    private String uploadDir;
    @Value("${upload.medium}")
    private int medium;
    @Value("${upload.small}")
    private int small;


    private UserAvatarRepository userAvatarRepository;
    private UserService userService;

    public UserAvatarServiceImpl(UserAvatarRepository userAvatarRepository,
                                 UserService userService) {
        this.userAvatarRepository = userAvatarRepository;
        this.userService = userService;
    }

    @Override
    public void save(UserAvatar avatar) {
        userAvatarRepository.save(avatar);
    }

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()){
            uploadPath.mkdir();
        }
        String resultFileName = UUID.randomUUID().toString() + StringUtils.cleanPath(file.getOriginalFilename());
        Path copyLocation = Paths
                .get(uploadDir + File.separator + resultFileName);
        Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
        return resultFileName;
    }

    public String thumbnailImage(String imageName, int size, MultipartFile file) throws IOException {
        String resultFileName = UUID.randomUUID().toString() + "__" +
                size + "px" + StringUtils.cleanPath(file.getOriginalFilename());
        String resultFileLocation = Paths
                .get(uploadDir + File.separator + resultFileName).toString();
        Thumbnails.of(uploadDir + File.separator + imageName)
                .size(size, size)
                .toFile(resultFileLocation);
        return resultFileName;
    }

    public void setAvatarToUser(User user, MultipartFile file) throws IOException {
        UserAvatar userAvatar = new UserAvatar();
        userAvatar.setOriginal(uploadImage(file));
        userAvatar.setMedium(thumbnailImage(userAvatar.getOriginal(), medium, file));
        userAvatar.setSmall(thumbnailImage(userAvatar.getOriginal(), small, file));
        save(userAvatar);
        user.setAvatar(userAvatar);
        userService.save(user);
    }

}
