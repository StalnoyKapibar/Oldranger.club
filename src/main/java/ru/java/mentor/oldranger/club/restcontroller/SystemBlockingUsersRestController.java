package ru.java.mentor.oldranger.club.restcontroller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.BlackListDto;
import ru.java.mentor.oldranger.club.dto.WritingBanDto;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BanType;
import ru.java.mentor.oldranger.club.model.utils.BlackList;
import ru.java.mentor.oldranger.club.model.utils.WritingBan;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.BlackListService;
import ru.java.mentor.oldranger.club.service.utils.WritingBanService;
import ru.java.mentor.oldranger.club.service.utils.impl.SessionService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class SystemBlockingUsersRestController {

    private UserService userService;
    private BlackListService blackListService;
    private WritingBanService writingBanService;
    private SessionService sessionService;

    @GetMapping("/admin/list")
    public ResponseEntity<List<User>> allUsers() {
        List<User> userList = userService.findAll();
        return ResponseEntity.ok(userList);
    }

    @PostMapping("/admin/blocking")
    public BlackListDto blockUser(@RequestBody BlackListDto blackListDto) {
        User user = userService.findById(blackListDto.getId());
        BlackList list = blackListService.findByUser(user);
        BlackList blackList;
        if (list == null && blackListDto.getDateUnblock().equals("")) {
            blackList = new BlackList(user, null);
        } else if (list != null && blackListDto.getDateUnblock().equals("")) {
            blackList = new BlackList(list.getId(), user, null);
        } else {
            String[] dateTime = blackListDto.getDateUnblock().split(" ");
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
        return blackListDto;
    }

    @PostMapping("/admin/writingBan")
    public WritingBanDto writingBanUser(@RequestBody WritingBanDto writingBanDto) {
        User user = userService.findById(writingBanDto.getId());
        BanType type = BanType.valueOf(writingBanDto.getBanType());
        WritingBan oldWritingBan = writingBanService.getByUserAndType(user, type);
        WritingBan writingBan;
        LocalDateTime localDateTime = null;
        if (writingBanDto.getDateUnblock()==null || writingBanDto.getDateUnblock().equals("")) {
            localDateTime = null;
        }
        else {
            String[] dateTime = writingBanDto.getDateUnblock().split(" ");
            String[] date = dateTime[0].split("/");
            String[] time = dateTime[1].split(":");
            localDateTime = LocalDateTime.of( Integer.parseInt(date[2]),
                    Integer.parseInt(date[1]),
                    Integer.parseInt(date[0]),
                    Integer.parseInt(time[0]),
                    Integer.parseInt(time[1]),
                    0);
        }
        if (oldWritingBan != null) {
            writingBan = new WritingBan(oldWritingBan.getId(), user, type, localDateTime);
        } else {
            writingBan = new WritingBan(user, type, localDateTime);
        }
        writingBanService.save(writingBan);
        return writingBanDto;
    }

    @GetMapping("/admin/blackList")
    public ResponseEntity<List<BlackList>> allBlockedUsers() {
        List<BlackList> blackList = blackListService.findAll();
        return ResponseEntity.ok(blackList);
    }
}
