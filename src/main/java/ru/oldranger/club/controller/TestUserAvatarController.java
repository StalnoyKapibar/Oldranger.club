package ru.oldranger.club.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.service.user.InvitationService;
import ru.oldranger.club.service.user.UserAvatarService;
import ru.oldranger.club.service.user.UserService;

import javax.servlet.http.HttpSession;

@Deprecated
@Hidden
@Controller
@RequestMapping("/test*")
public class TestUserAvatarController {

    // Тестирование добавления, обновления и удаления аватарки в профиле
    @Autowired
    UserAvatarService userAvatarService;
    @Autowired
    UserService userService;
    @Autowired
    InvitationService invitationService;

    @GetMapping("/profile")
    public String getTestProfile(HttpSession session,
                                 @ModelAttribute("message") String message,
                                 Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        User user = userService.getUserByNickName(name);
        String key = invitationService.getCurrentKey(user);
        session.setAttribute("user", user);
        session.setAttribute("key", key);
        model.addAttribute("message", message + "");
        return "profile";
    }


    @PostMapping("/uploadAvatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file,
                               RedirectAttributes redirectAttributes,
                               @SessionAttribute User user) {

        if (user.getAvatar() == null) {
            userAvatarService.setAvatarToUser(user, file);
        } else {
            userAvatarService.updateUserAvatar(user, file);
        }

        redirectAttributes.addFlashAttribute("message", "Аватар " + file.getOriginalFilename() + " успешно загружен!");
        return "redirect:/test/profile";
    }

    @GetMapping("/deleteAvatar")
    public String deleteAvatar(@SessionAttribute User user,
                               RedirectAttributes redirectAttributes) {

        userAvatarService.deleteUserAvatar(user);

        redirectAttributes.addFlashAttribute("message", "Аватар удален!");
        return "redirect:/test/profile";
    }
}