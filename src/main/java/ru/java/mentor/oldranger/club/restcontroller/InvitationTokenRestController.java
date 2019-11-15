package ru.java.mentor.oldranger.club.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.model.user.InvitationToken;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.mail.MailService;
import ru.java.mentor.oldranger.club.service.user.InvitationService;
import ru.java.mentor.oldranger.club.service.user.UserService;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/invite")
public class InvitationTokenRestController {

    private InvitationService invitationService;

    private UserService userService;

    private MailService mailService;

    @Value("${server.protocol}")
    private String protocol;

    @Value("${server.name}")
    private String host;

    @Value("${server.port}")
    private String port;

    @Autowired
    public void setMailService(MailService service) {
        this.mailService = service;
    }

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
        String key = invitationService.getCurrentKey(user);
        if (key == null) {
            key = invitationService.generateKey();
            InvitationToken invitationToken = new InvitationToken(key, user);
            invitationService.save(invitationToken);
        }
        return key;
    }

    @RequestMapping(value = "/bymail", method = RequestMethod.POST)
    public String sendInviteByMail(@RequestBody String mail) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        User user = userService.getUserByNickName(userName);
        //  String message = "Привет! Это приглашение на форум. Для регистрации пройди по ссылке:\n";
        String key = invitationService.generateKey();
        String link = protocol + "://" + host + ":" + port + "/invite?key=" + key;
        InvitationToken invitationToken = new InvitationToken(key, user, mail);
        String status = mailService.sendHtmlEmail(mail, userName, "letter.html", link);
        invitationService.markInviteOnMailAsUsed(mail);
        invitationService.save(invitationToken);
        return status;
    }

    @GetMapping("/tree")
    public ResponseEntity<Map<String, Set<String>>> getSectionsAndSubsectionsDto() {
        Map<String, Set<String>> dtos = invitationService.getFullInvitationTree();
        if (dtos == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dtos);

    }
}
