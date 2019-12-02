package ru.java.mentor.oldranger.club.restcontroller;

import lombok.AllArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.*;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.forum.TopicVisitAndSubscription;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserProfile;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.forum.TopicVisitAndSubscriptionService;
import ru.java.mentor.oldranger.club.service.user.*;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserProfileRestController {

    private UserProfileService userProfileService;
    private UserStatisticService userStatisticService;
    private UserService userService;
    private InvitationService invitationService;
    private TopicService topicService;
    private TopicVisitAndSubscriptionService topicVisitAndSubscriptionService;
    private CommentService commentService;
    private PasswordEncoder passwordEncoder;
    private SecurityUtilsService securityUtilsService;


    @InitBinder
    // передаем пустые строки как null
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDto> getProfile() {

        User user = securityUtilsService.getLoggedUser();
        if (user == null) return ResponseEntity.noContent().build();
        UserProfile profile = userProfileService.getUserProfileByUser(user);
        UserStatistic stat = userStatisticService.getUserStaticByUser(user);
        ProfileDto dto = userProfileService.buildProfileDto(profile, stat, true);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileDto> getAnotherUserProfile(@PathVariable Long id) {
        User user;
        try {
            user = userService.findById(id);
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
        UserProfile profile = userProfileService.getUserProfileByUser(user);
        UserStatistic stat = userStatisticService.getUserStaticByUser(user);
        ProfileDto dto = userProfileService.buildProfileDto(profile, stat, false);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/updateProfile")
    public ResponseEntity<UserProfile> getUpdateForm() {
        User user = securityUtilsService.getLoggedUser();
        if (user == null) return ResponseEntity.noContent().build();
        UserProfile profile = userProfileService.getUserProfileByUser(user);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<UpdateProfileDto> updateProfile(UserProfile profile) {

        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) return ResponseEntity.noContent().build();

        if (profile.getUser().getNickName() == null || profile.getUser().getEmail() == null){
            UpdateProfileDto dto = new UpdateProfileDto(profile, new ErrorDto("Поля 'Ник' и 'Email' обязательно должны быть заполнены"));
            return ResponseEntity.ok(dto);
        }
        User user = userService.getUserByNickName(profile.getUser().getNickName());
        if (user != null && !currentUser.getNickName().equals(user.getNickName())) {
            UpdateProfileDto dto = new UpdateProfileDto(profile, new ErrorDto("Пользователь с таким ником уже существует"));
            return ResponseEntity.ok(dto);
        }
        user = userService.getUserByEmail(profile.getUser().getEmail());
        if (user != null && !currentUser.getEmail().equals(user.getEmail())) {
            UpdateProfileDto dto = new UpdateProfileDto(profile, new ErrorDto("Пользователь с таким адресом почты уже существует"));
            return ResponseEntity.ok(dto);
        }
        currentUser.setNickName(profile.getUser().getNickName());
        currentUser.setFirstName(profile.getUser().getFirstName());
        currentUser.setLastName(profile.getUser().getLastName());
        currentUser.setEmail(profile.getUser().getEmail());
        userService.save(currentUser);
        profile.setUser(currentUser);
        userProfileService.editUserProfile(profile);
        UpdateProfileDto dto = new UpdateProfileDto(profile, null);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/comments")
    public ResponseEntity<List<CommentDto>> getComments(
                                            @RequestAttribute(value = "page", required = false) Integer page,
                                            @PageableDefault(size = 10, sort = "dateTime",direction = Sort.Direction.DESC) Pageable pageable) {
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) return ResponseEntity.noContent().build();

        List<CommentDto> dtos = commentService.getPageableCommentDtoByUser(currentUser, pageable).getContent();
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/subscriptions")
    public ResponseEntity<List<TopicVisitAndSubscription>> getSubscriptions(
                                   @RequestAttribute(value = "page", required = false) Integer page,
                                   @PageableDefault(size = 10) Pageable pageable) {
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) return ResponseEntity.noContent().build();

        if (page != null) {
            pageable = PageRequest.of(page, 10, Sort.by("lastMessageTime"));
        }

        List<TopicVisitAndSubscription> dtos = topicVisitAndSubscriptionService.getPagebleTopicVisitAndSubscriptionForUser(currentUser,pageable).getContent();
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/topics")
    public ResponseEntity<List<Topic>> getTopics(
                            @RequestAttribute(value = "page", required = false) Integer page,
                            @PageableDefault(size = 10, sort = "lastMessageTime", direction = Sort.Direction.DESC) Pageable pageable) {
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) return ResponseEntity.noContent().build();

        if (page != null) {
            pageable = PageRequest.of(page, 10, Sort.by("lastMessageTime"));
        }
        List<Topic> dtos = topicService.findAllTopicsStartedByUser(currentUser,pageable).getContent();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/invite")
    public ResponseEntity<InviteDto> getInvitation() {
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) return ResponseEntity.noContent().build();

        String key = invitationService.getCurrentKey(currentUser);
        InviteDto dto = new InviteDto(currentUser, key);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/changePassword")
    public String getChangePasswordForm() {
        return "profile/changePassword";
    }

    @PostMapping("/changePassword")
    public ResponseEntity<ErrorDto> changePassword(@RequestParam String oldPass,
                                                   @RequestParam String newPass,
                                                   @RequestParam String passConfirm) {
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) return ResponseEntity.noContent().build();

        if (passwordEncoder.matches(oldPass,currentUser.getPassword())){
            if (passConfirm.equals(newPass)){
                currentUser.setPassword(passwordEncoder.encode(newPass));
                userService.save(currentUser);
                return ResponseEntity.ok(new ErrorDto("Пароль обновлен"));
            }
        }
        return ResponseEntity.ok(new ErrorDto("Пароль указан неверно"));
    }

}
