package ru.java.mentor.oldranger.club.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BlackList;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.BlackListService;
import ru.java.mentor.oldranger.club.service.utils.impl.SessionService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class TestSystemBlockingUsers {

    // пример с блокировкой пользователя
    private UserService userService;
    private BlackListService blackListService;
    private SessionService sessionService;

    @Autowired
    public TestSystemBlockingUsers(BlackListService blackListService, UserService userService, SessionService sessionService) {
        this.blackListService = blackListService;
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @GetMapping("/admin")
    public String allUsers(Model model) {
        List<User> userList = userService.findAll();
        List<BlackList> blackList = blackListService.findAll();
        model.addAttribute("list", userList);
        model.addAttribute("blackList", blackList);
        return "testBlockingUsers";
    }

    @PostMapping("/admin/blocking")
    public String blockUser(@RequestParam(value = "id") Long id,
                            @RequestParam(value = "datetimepicker", required = false) String dateUnblock) {
        User user = userService.findById(id);
        BlackList list = blackListService.findByUser(user);
        BlackList blackList;
        if (list == null && dateUnblock.equals("")) {
            blackList = new BlackList(user, null);
        } else if (list != null && dateUnblock.equals("")) {
            blackList = new BlackList(list.getId(), user, null);
        } else {
            String[] dateTime = dateUnblock.split(" ");
            String[] date = dateTime[0].split("/");
            String[] time = dateTime[1].split(":");
            LocalDateTime localDateTime = LocalDateTime.of( Integer.parseInt(date[2]),
                                                            Integer.parseInt(date[1]),
                                                            Integer.parseInt(date[0]),
                                                            Integer.parseInt(time[0]),
                                                            Integer.parseInt(time[1]),
                                                    0);
            if (list != null) {
                blackList = new BlackList(list.getId(), user, localDateTime);
            } else {
                blackList = new BlackList(user, localDateTime);
            }
        }
        blackListService.save(blackList);
        sessionService.expireUserSessions(user.getUsername());
        return "redirect:/admin";
    }

    @GetMapping("/admin/del({id})")
    public String deleteBlock(@PathVariable Long id) {
        blackListService.deleteBlock(id);
        return "redirect:/admin";
    }
}