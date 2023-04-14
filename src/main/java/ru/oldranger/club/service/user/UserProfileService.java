package ru.oldranger.club.service.user;

import ru.oldranger.club.dto.ProfileDto;
import ru.oldranger.club.dto.UpdateProfileDto;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.model.user.UserProfile;
import ru.oldranger.club.model.user.UserStatistic;

public interface UserProfileService {

    UserProfile createUserProfile(UserProfile userProfile);

    void deleteUserProfileById(Long id);

    void saveUserProfile(UserProfile userProfile);

    void updateUserProfile(UserProfile userProfile, UpdateProfileDto updateProfileDto);

    UserProfile getUserProfileByUser(User user);

    ProfileDto buildProfileDto(UserProfile profile, UserStatistic stat, boolean owner, boolean isUser);
}