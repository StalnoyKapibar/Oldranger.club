package ru.java.mentor.oldranger.club.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.user.InvitationService;
import ru.java.mentor.oldranger.club.service.user.UserService;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
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
    @ResponseBody
    public void addUser(@RequestBody String key) {
        User user = new User(); //это "тестовый" юзер. Надо добавить получение данных юзера из формы
        user.setEmail("email@mail.ru");
        user.setNickName("Nick");
        userService.save(user);
        invitationService.getInvitationTokenByKey(key).setVisitor(user);
        invitationService.markAsUsed(key);
    }
}
