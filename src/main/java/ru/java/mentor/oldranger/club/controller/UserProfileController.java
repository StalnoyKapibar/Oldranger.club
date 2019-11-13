package ru.java.mentor.oldranger.club.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserProfile;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.user.UserAvatarService;
import ru.java.mentor.oldranger.club.service.user.UserProfileService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import java.io.IOException;


@ControllerAdvice
@Controller
@AllArgsConstructor
public class UserProfileController {

    UserAvatarService userAvatarService;
    UserProfileService userProfileService;
    UserStatisticService userStatisticService;
    UserService userService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("/profile")
    public String getProfile(@AuthenticationPrincipal User user, Model model) {
        UserProfile profile = userProfileService.getUserProfileByUser(user);
        UserStatistic stat = userStatisticService.getUserStaticByUser(user);
        model.addAttribute("profile", profile);
        model.addAttribute("stat", stat);
        return "profile";
    }

    @PostMapping("/uploadAvatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file,
                               @AuthenticationPrincipal User user) {
        try {
            userAvatarService.updateUserAvatar(user, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/profile";
    }

    @GetMapping("/deleteAvatar")
    public String deleteAvatar(@AuthenticationPrincipal User user) {
        try {
            userAvatarService.deleteUserAvatar(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/profile";
    }

    @GetMapping("/updateProfile")
    public String getUpdateForm(@AuthenticationPrincipal User user, Model model) {
        UserProfile profile = userProfileService.getUserProfileByUser(user);
        model.addAttribute("profile", profile);
        return "updateUserProfile";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(UserProfile profile,
                                @AuthenticationPrincipal User user) {
        user.setNickName(profile.getUser().getNickName());
        user.setFirstName(profile.getUser().getFirstName());
        user.setLastName(profile.getUser().getLastName());
        user.setEmail(profile.getUser().getEmail());
        userService.save(user);
        profile.setUser(user);
        userProfileService.editUserProfile(profile);
        return "redirect:/profile";
    }


}
