package ru.java.mentor.oldranger.club.service.user;

import ru.java.mentor.oldranger.club.dto.ProfileDto;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserProfile;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;

public interface UserProfileService {

    UserProfile createUserProfile(UserProfile userProfile);

    void deleteUserProfileById(Long id);

    void saveUserProfile(UserProfile userProfile);

    UserProfile getUserProfileByUser(User user);

    ProfileDto buildProfileDto(UserProfile profile, UserStatistic stat, boolean owner, boolean isUser);
}