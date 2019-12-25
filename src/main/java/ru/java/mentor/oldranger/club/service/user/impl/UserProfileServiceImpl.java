package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(UserAvatarServiceImpl.class);
    private UserProfileRepository userProfileRepository;

    @Override
    public UserProfile createUserProfile(UserProfile userProfile) {
        LOG.info("Saving user profile");
        UserProfile profile = null;
        try {
            profile = userProfileRepository.save(userProfile);
            LOG.info("Profile {} saved", profile);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return profile;
    }

    @Override
    public void deleteUserProfileById(Long id) {
        LOG.info("Deleting profile with id = {}", id);
        try {
            userProfileRepository.deleteById(id);
            LOG.info("Profile deleted");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void editUserProfile(UserProfile userProfile) {
        LOG.info("Updating profile with id = {}", userProfile.getId());
        try {
            userProfileRepository.save(userProfile);
            LOG.info("Profile updated");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public UserProfile getUserProfileByUser(User user) {
        LOG.debug("Getting profile for user with id = {}", user.getId());
        UserProfile profile = null;
        try {
            profile = userProfileRepository.getOneByUser(user);
            LOG.debug("Profile returned");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return profile;
    }

    @Override
    public ProfileDto buildProfileDto(UserProfile profile, UserStatistic stat, boolean owner, boolean isUser) {
        LOG.debug("Building profile dto");
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