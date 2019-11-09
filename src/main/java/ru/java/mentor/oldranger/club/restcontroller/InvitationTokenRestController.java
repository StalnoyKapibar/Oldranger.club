package ru.java.mentor.oldranger.club.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.java.mentor.oldranger.club.model.user.InvitationToken;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.user.InvitationService;
import ru.java.mentor.oldranger.club.service.user.UserService;

@RestController
@RequestMapping("/api/invite")
public class InvitationTokenRestController {

    private InvitationService invitationService;

    private UserService userService;

    @Autowired
    public void setUserService(UserService service) {
        this.userService = service;
    }

    @Autowired
    public void setInvitationService(InvitationService service) {
        this.invitationService = service;
    }

    @PostMapping
    public String saveInvitationToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        User user = userService.getUserByNickName(name);
        String key = invitationService.getCurrentKey();
        if (key != null) {
            InvitationToken invitationToken = new InvitationToken(key, user);
            invitationService.save(invitationToken);
        }
        return key;
    }
}
