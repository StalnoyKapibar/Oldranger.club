package ru.java.mentor.oldranger.club.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.dto.CommentDto;
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

@Deprecated
@Hidden
@Controller
@RequestMapping("/profile")
@AllArgsConstructor
public class UserProfileController {

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

    @GetMapping
    public String getProfile(@AuthenticationPrincipal User user,
                             HttpSession session,
                             Model model) {
        UserProfile profile = userProfileService.getUserProfileByUser(user);
        UserStatistic stat = userStatisticService.getUserStaticByUser(user);
        session.setAttribute("currentUser",user);
        model.addAttribute("profile", profile);
        model.addAttribute("owner", true);
        model.addAttribute("stat", stat);
        return "profile/profileInfo";
    }

    @GetMapping("/{id}")
    public String getAnotherUserProfile(@PathVariable Long id, Model model) {
        User user;
        try {
            user = userService.findById(id);
        } catch (Exception e){
            model.addAttribute("message", "Такого пользователя не существует");
            return "404";
        }
        UserProfile profile = userProfileService.getUserProfileByUser(user);
        UserStatistic stat = userStatisticService.getUserStaticByUser(user);
        model.addAttribute("profile", profile);
        model.addAttribute("owner", false);
        model.addAttribute("stat", stat);
        return "profile/profileInfo";
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
    public String getUpdateForm(@SessionAttribute User currentUser, Model model) {
        UserProfile profile = userProfileService.getUserProfileByUser(currentUser);
        model.addAttribute("profile", profile);
        return "profile/updateUserProfile";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@SessionAttribute User currentUser,
                                UserProfile profile,
                                Model model) {
        if (profile.getUser().getNickName() == null || profile.getUser().getEmail() == null){
            model.addAttribute("profile", profile);
            model.addAttribute("err","Поля 'Ник' и 'Email' обязательно должны быть заполнены");
            return "profile/updateUserProfile";
        }
        User user = userService.getUserByNickName(profile.getUser().getNickName());
        if (user != null && !currentUser.getNickName().equals(user.getNickName())) {
            model.addAttribute("profile", profile);
            model.addAttribute("err","Пользователь с таким ником уже существует");
            return "profile/updateUserProfile";
        }
        user = userService.getUserByEmail(profile.getUser().getEmail());
        if (user != null && !currentUser.getEmail().equals(user.getEmail())) {
            model.addAttribute("profile", profile);
            model.addAttribute("err","Пользователь с таким адресом почты уже существует");
            return "profile/updateUserProfile";
        }
        currentUser.setNickName(profile.getUser().getNickName());
        currentUser.setFirstName(profile.getUser().getFirstName());
        currentUser.setLastName(profile.getUser().getLastName());
        currentUser.setEmail(profile.getUser().getEmail());
        userService.save(currentUser);
        profile.setUser(currentUser);
        userProfileService.editUserProfile(profile);
        return "redirect:/profile";
    }

    @GetMapping("/comments")
    public String getComments(@SessionAttribute User currentUser,
                              @RequestAttribute(value = "page", required = false) Integer page,
                              @PageableDefault(size = 10, sort = "dateTime",direction = Sort.Direction.DESC) Pageable pageable,
                              Model model) {
        Page<CommentDto> dtos = commentService.getPageableCommentDtoByUser(currentUser, pageable);
        model.addAttribute("pageCount", dtos.getTotalPages());
        model.addAttribute("commentList", dtos.getContent());
        return "profile/profileMyComments";
    }


    @GetMapping("/subscriptions")
    public String getSubscriptions(@SessionAttribute User currentUser,
                                   @RequestAttribute(value = "page", required = false) Integer page,
                                   @PageableDefault(size = 10) Pageable pageable,
                                   Model model) {
        if (page != null) {
            pageable = PageRequest.of(page, 10, Sort.by("lastMessageTime"));
        }

        Page<TopicVisitAndSubscription> sub = topicVisitAndSubscriptionService.getPagebleTopicVisitAndSubscriptionForUser(currentUser,pageable);
        model.addAttribute("pageCount", sub.getTotalPages());
        model.addAttribute("subList", sub.getContent());
        return "profile/profileMySubscriptions";
    }


    @GetMapping("/topics")
    public String getTopics(@SessionAttribute User currentUser,
                            @RequestAttribute(value = "page", required = false) Integer page,
                            @PageableDefault(size = 10, sort = "lastMessageTime", direction = Sort.Direction.DESC) Pageable pageable,
                            Model model) {
        if (page != null) {
            pageable = PageRequest.of(page, 10, Sort.by("lastMessageTime"));
        }
        Page<Topic> topics = topicService.findAllTopicsStartedByUser(currentUser,pageable);
        model.addAttribute("pageCount", topics.getTotalPages());
        model.addAttribute("topicList", topics.getContent());
        return "profile/profileMyTopics";
    }

    @GetMapping("/invite")
    public String getIvitation(@SessionAttribute User currentUser, Model model) {
        String key = invitationService.getCurrentKey(currentUser);
        model.addAttribute("key",key);
        model.addAttribute("user", currentUser);
        return "profile/profileMyInvitation";
    }

    @GetMapping("/changePassword")
    public String getChangePasswordForm() {
        return "profile/changePassword";
    }

    @PostMapping("/changePassword")
    public String changePassword(@SessionAttribute User currentUser,
                                 @RequestParam String oldPass,
                                 @RequestParam String newPass,
                                 @RequestParam String passConfirm,
                                 Model model) {
        if (passwordEncoder.matches(oldPass,currentUser.getPassword())){
            if (passConfirm.equals(newPass)){
                currentUser.setPassword(passwordEncoder.encode(newPass));
                userService.save(currentUser);
                return "redirect:/profile";
            }
        }
        model.addAttribute("err","Пароль указан неверно");
        return "profile/changePassword";
    }

}
