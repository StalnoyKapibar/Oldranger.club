package ru.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.oldranger.club.dao.UserRepository.UserProfileRepository;
import ru.oldranger.club.dto.ProfileDto;
import ru.oldranger.club.dto.UpdateProfileDto;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.model.user.UserProfile;
import ru.oldranger.club.model.user.UserStatistic;
import ru.oldranger.club.service.user.UserProfileService;

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
    public void saveUserProfile(UserProfile userProfile) {
        log.info("Saving user profile with id = {}", userProfile.getId());
        try {
            userProfileRepository.save(userProfile);
            log.info("Profile saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void updateUserProfile(UserProfile userProfile, UpdateProfileDto updateProfileDto) {
        log.info("Updating user profile with id = {}", userProfile.getId());
        userProfile.setCity(updateProfileDto.getCity());
        userProfile.setCountry(updateProfileDto.getCountry());
        userProfile.setBirthday(updateProfileDto.getBirthday());
        userProfile.setGender(updateProfileDto.getGender());
        userProfile.setPhoneNumber(updateProfileDto.getPhoneNumber());
        userProfile.setSocialFb(updateProfileDto.getSocialFb());
        userProfile.setSocialTw(updateProfileDto.getSocialTw());
        userProfile.setSocialVk(updateProfileDto.getSocialVk());
        userProfile.setAboutMe(updateProfileDto.getAboutMe());

        saveUserProfile(userProfile);
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
                profile.getCity(),
                profile.getCountry(),
                profile.getBirthday(),
                profile.getGender(),
                profile.getPhoneNumber(),
                profile.getSocialFb(),
                profile.getSocialTw(),
                profile.getSocialVk(),
                profile.getAboutMe(),
                profile.getUser().getRegDate(),
                stat.getMessageCount(),
                stat.getTopicStartCount(),
                stat.getLastComment(),
                stat.getLastVisit(),
                profile.getUser().getAvatar().getOriginal(),
                owner,
                isUser);
    }

}