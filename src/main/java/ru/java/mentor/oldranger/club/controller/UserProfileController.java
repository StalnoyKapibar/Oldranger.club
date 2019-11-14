package ru.java.mentor.oldranger.club.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.forum.TopicVisitAndSubscription;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserProfile;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.forum.TopicVisitAndSubscriptionService;
import ru.java.mentor.oldranger.club.service.user.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;


@ControllerAdvice
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
        model.addAttribute("stat", stat);
        return "profile/profileInfo";
    }

    @PostMapping("/uploadAvatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file,
                               @SessionAttribute User currentUser) {
        try {
            userAvatarService.updateUserAvatar(currentUser, file);
        } catch (IOException e) {
            e.printStackTrace();
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
    public String updateProfile(UserProfile profile,
                                @SessionAttribute User currentUser) {
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
    public String getComments(@SessionAttribute User currentUser, Model model) {
        String key = invitationService.getCurrentKey(currentUser);
        model.addAttribute("key",key);
        model.addAttribute("user", currentUser);
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
                            @PageableDefault(size = 10, sort = "lastMessageTime") Pageable pageable,
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
    public String getIvitation(@AuthenticationPrincipal User user, Model model) {
        String key = invitationService.getCurrentKey(user);
        model.addAttribute("key",key);
        model.addAttribute("user", user);
        return "profile/profileMyInvitation";
    }




}
