package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.UserProfileRepository;
import ru.java.mentor.oldranger.club.dto.ProfileDto;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserProfile;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.user.UserProfileService;

@Slf4j
@Service
@AllArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private UserProfileRepository userProfileRepository;

    @Override
    public UserProfile createUserProfile(UserProfile userProfile) {
        log.info("Saving user profile");
        UserProfile profile = null;
        try {
            profile = userProfileRepository.save(userProfile);
            log.info("Profile {} saved", profile);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return profile;
    }

    @Override
    public void deleteUserProfileById(Long id) {
        log.info("Deleting profile with id = {}", id);
        try {
            userProfileRepository.deleteById(id);
            log.info("Profile deleted");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void editUserProfile(UserProfile userProfile) {
        log.info("Updating profile with id = {}", userProfile.getId());
        try {
            userProfileRepository.save(userProfile);
            log.info("Profile updated");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public UserProfile getUserProfileByUser(User user) {
        log.debug("Getting profile for user with id = {}", user.getId());
        UserProfile profile = null;
        try {
            profile = userProfileRepository.getOneByUser(user);
            log.debug("Profile returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return profile;
    }

    @Override
    public ProfileDto buildProfileDto(UserProfile profile, UserStatistic stat, boolean owner, boolean isUser) {
        log.debug("Building profile dto");
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
                isUser,
                profile.getCity(),
                profile.getCountry(),
                profile.getAboutMe());
    }

}