package ru.java.mentor.oldranger.club.service.user;

import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserProfile;

public interface UserProfileService {

    UserProfile createUserProfile(UserProfile userProfile);

    void deleteUserProfileById(Long id);

    void editUserProfile(UserProfile userProfile);

    UserProfile getUserProfileByUser(User user);
}