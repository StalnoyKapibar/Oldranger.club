package ru.java.mentor.oldranger.club.service.user;

import ru.java.mentor.oldranger.club.model.user.UserProfile;

public interface UserProfileService {

    public UserProfile createUserProfile(UserProfile userProfile);

    public void deleteUserProfileById(Long id);

    public void editUserProfile(UserProfile userProfile);
}
