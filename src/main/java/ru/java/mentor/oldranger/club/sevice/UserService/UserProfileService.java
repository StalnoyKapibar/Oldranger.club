package ru.java.mentor.oldranger.club.sevice.UserService;

import ru.java.mentor.oldranger.club.model.user.UserProfile;

public interface UserProfileService {

    public void createUserProfile(UserProfile userProfile);

    public void deleteUserProfileById(Long id);

    public void editUserProfileById(Long id);
}
