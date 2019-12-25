package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.UserProfileRepository;
import ru.java.mentor.oldranger.club.dto.ProfileDto;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserProfile;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.user.UserProfileService;

@Service
@AllArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

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

    @Override
    public UserProfile getUserProfileByUser(User user) {
        return userProfileRepository.getOneByUser(user);
    }

    @Override
    public ProfileDto buildProfileDto(UserProfile profile, UserStatistic stat, boolean owner, boolean isUser) {
        return new ProfileDto(profile.getUser().getId(),
                profile.getUser().getNickName(),
                profile.getUser().getFirstName(),
                profile.getUser().getLastName(),
                profile.getUser().getEmail(),
                profile.getUser().getRegDate(),
                stat.getMessageCount(),
                stat.getTopicStartCount(),
                stat.getLastComment(),
                stat.getLastVizit(),
                profile.getUser().getAvatar().getOriginal(),
                owner,
                isUser);
    }

}