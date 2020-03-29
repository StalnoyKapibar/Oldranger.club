package ru.java.mentor.oldranger.club.service.user;

import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserAvatar;

public interface UserAvatarService {
    void save(UserAvatar avatar);

    String uploadImage(MultipartFile file, Long id);

    void setAvatarToUser(User user, MultipartFile file);

    void deleteUserAvatar(User user);

    UserAvatar setDefaultAvatar(Long id);

    void updateUserAvatar(User user, MultipartFile file);
}