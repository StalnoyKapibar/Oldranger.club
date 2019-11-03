package ru.java.mentor.oldranger.club.service.user;

import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserAvatar;

import java.io.IOException;

public interface UserAvatarService {
    void save(UserAvatar avatar);
    String uploadImage(MultipartFile file) throws IOException;
    String thumbnailImage(String imageLocation, int size, MultipartFile file) throws IOException;
    void setAvatarToUser(User user, MultipartFile file) throws IOException;
    void deleteUserAvatar(User user) throws IOException;
    void updateUserAvatar(User user, MultipartFile file) throws IOException;
}
