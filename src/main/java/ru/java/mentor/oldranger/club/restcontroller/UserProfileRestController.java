package ru.java.mentor.oldranger.club.restcontroller;

import lombok.AllArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserProfileRestController {

    private UserAvatarService userAvatarService;
    private UserProfileService userProfileService;
    private UserStatisticService userStatisticService;
    private UserService userService;
    private InvitationService invitationService;
    private TopicService topicService;
    private TopicVisitAndSubscriptionService topicVisitAndSubscriptionService;
    private CommentService commentService;
    private PasswordEncoder passwordEncoder;


    @InitBinder
    // передаем пустые строки как null
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDto> getProfile(@AuthenticationPrincipal User user,
                                                 HttpSession session) {
        UserProfile profile = userProfileService.getUserProfileByUser(user);
        UserStatistic stat = userStatisticService.getUserStaticByUser(user);
        session.setAttribute("currentUser",user);
        ProfileDto dto = new ProfileDto(profile.getUser().getNickName(),
                                        profile.getUser().getFirstName(),
                                        profile.getUser().getLastName(),
                                        profile.getUser().getEmail(),
                                        profile.getUser().getRegDate(),
                                        stat.getMessageCount(),
                                        stat.getTopicStartCount(),
                                        stat.getLastComment(),
                                        stat.getLastVizit(),
                                        profile.getUser().getAvatar().getOriginal(),
                                        true);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileDto> getAnotherUserProfile(@PathVariable Long id, Model model) {
        User user;
        try {
            user = userService.findById(id);
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
        UserProfile profile = userProfileService.getUserProfileByUser(user);
        UserStatistic stat = userStatisticService.getUserStaticByUser(user);
        ProfileDto dto = new ProfileDto(profile.getUser().getNickName(),
                profile.getUser().getFirstName(),
                profile.getUser().getLastName(),
                profile.getUser().getEmail(),
                profile.getUser().getRegDate(),
                stat.getMessageCount(),
                stat.getTopicStartCount(),
                stat.getLastComment(),
                stat.getLastVizit(),
                profile.getUser().getAvatar().getOriginal(),
                false);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/uploadAvatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file,
                               @SessionAttribute User currentUser) {
        if (("image/jpeg").equals(file.getContentType()) || ("image/png").equals(file.getContentType())){
            try {
                userAvatarService.updateUserAvatar(currentUser, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/profile";
    }

    @GetMapping("/deleteAvatar")
    public String deleteAvatar(@SessionAttribute User currentUser) {
        try {
            userAvatarService.deleteUserAvatar(currentUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/profile";
    }

    @GetMapping("/updateProfile")
    public ResponseEntity<UserProfile> getUpdateForm(@SessionAttribute User currentUser) {
        UserProfile profile = userProfileService.getUserProfileByUser(currentUser);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<UpdateProfileDto> updateProfile(@SessionAttribute User currentUser,
                                                          UserProfile profile) {
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
    public ResponseEntity<List<CommentDto>> getComments(@SessionAttribute User currentUser,
                                            @RequestAttribute(value = "page", required = false) Integer page,
                                            @PageableDefault(size = 10, sort = "dateTime",direction = Sort.Direction.DESC) Pageable pageable,
                                            Model model) {
        List<CommentDto> dtos = commentService.getPageableCommentDtoByUser(currentUser, pageable).getContent();
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/subscriptions")
    public ResponseEntity<List<TopicVisitAndSubscription>> getSubscriptions(@SessionAttribute User currentUser,
                                   @RequestAttribute(value = "page", required = false) Integer page,
                                   @PageableDefault(size = 10) Pageable pageable) {
        if (page != null) {
            pageable = PageRequest.of(page, 10, Sort.by("lastMessageTime"));
        }

        List<TopicVisitAndSubscription> dtos = topicVisitAndSubscriptionService.getPagebleTopicVisitAndSubscriptionForUser(currentUser,pageable).getContent();
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/topics")
    public ResponseEntity<List<Topic>> getTopics(@SessionAttribute User currentUser,
                            @RequestAttribute(value = "page", required = false) Integer page,
                            @PageableDefault(size = 10, sort = "lastMessageTime", direction = Sort.Direction.DESC) Pageable pageable) {
        if (page != null) {
            pageable = PageRequest.of(page, 10, Sort.by("lastMessageTime"));
        }
        List<Topic> dtos = topicService.findAllTopicsStartedByUser(currentUser,pageable).getContent();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/invite")
    public ResponseEntity<InviteDto> getIvitation(@SessionAttribute User currentUser) {
        String key = invitationService.getCurrentKey(currentUser);
        InviteDto dto = new InviteDto(currentUser, key);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/changePassword")
    public String getChangePasswordForm() {
        return "profile/changePassword";
    }

    @PostMapping("/changePassword")
    public ResponseEntity<ErrorDto> changePassword(@SessionAttribute User currentUser,
                                                   @RequestParam String oldPass,
                                                   @RequestParam String newPass,
                                                   @RequestParam String passConfirm) {
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
