package ru.java.mentor.oldranger.club.restcontroller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BlackList;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.BlackListService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class SystemBlockingUsersRestController {

    private UserService userService;
    private BlackListService blackListService;

    @GetMapping("/admin/list")
    public ResponseEntity<List<User>> allUsers() {
        List<User> userList = userService.findAll();
        return ResponseEntity.ok(userList);
    }

    @GetMapping("/admin/blackList")
    public ResponseEntity<List<BlackList>> allBlockedUsers() {
        List<BlackList> blackList = blackListService.findAll();
        return ResponseEntity.ok(blackList);
    }
}
