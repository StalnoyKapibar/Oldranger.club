package ru.java.mentor.oldranger.club.service.user.impl;

import ru.java.mentor.oldranger.club.dao.UserRepository.UserAvatarRepository;
import ru.java.mentor.oldranger.club.model.user.UserAvatar;
import ru.java.mentor.oldranger.club.service.user.UserAvatarService;

public class UserAvatarServiceImpl implements UserAvatarService {

    private UserAvatarRepository userAvatarRepository;

    public UserAvatarServiceImpl(UserAvatarRepository userAvatarRepository) {
        this.userAvatarRepository = userAvatarRepository;
    }

    @Override
    public void save(UserAvatar avatar) {
        userAvatarRepository.save(avatar);
    }
}
