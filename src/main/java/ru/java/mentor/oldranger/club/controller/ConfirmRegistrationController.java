package ru.java.mentor.oldranger.club.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.java.mentor.oldranger.club.service.user.InvitationService;
import ru.java.mentor.oldranger.club.service.user.RoleService;
import ru.java.mentor.oldranger.club.service.user.UserService;

@Deprecated
@Controller
@Getter
@Setter
@AllArgsConstructor
@RequestMapping("/confirm")
public class ConfirmRegistrationController {
    private InvitationService invitationService;
    private UserService userService;
    private RoleService roleService;

    @GetMapping
    public String getConfirmForm(@RequestParam(name = "key") String key) {
        return "confirm";
    }
}
