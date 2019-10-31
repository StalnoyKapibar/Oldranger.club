package ru.java.mentor.oldranger.club.sevice.UserService.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.UserProfileRepository;
import ru.java.mentor.oldranger.club.model.user.UserProfile;
import ru.java.mentor.oldranger.club.sevice.UserService.UserProfileService;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Override
    public UserProfile createUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    @Override
    public void deleteUserProfileById(Long id) {
        userProfileRepository.deleteById(id);
    }

    @Override
    public void editUserProfile(UserProfile userProfile) {
        userProfileRepository.save(userProfile);
    }
}
