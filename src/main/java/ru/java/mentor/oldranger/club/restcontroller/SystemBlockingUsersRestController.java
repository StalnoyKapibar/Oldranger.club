package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.BlackListDto;
import ru.java.mentor.oldranger.club.dto.WritingBanDto;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BanType;
import ru.java.mentor.oldranger.club.model.utils.BlackList;
import ru.java.mentor.oldranger.club.model.utils.WritingBan;
import ru.java.mentor.oldranger.club.service.user.RoleService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.BlackListService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;
import ru.java.mentor.oldranger.club.service.utils.WritingBanService;
import ru.java.mentor.oldranger.club.service.utils.impl.SessionService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Tag(name = "System blocking users")
public class SystemBlockingUsersRestController {

    private UserService userService;
    private BlackListService blackListService;
    private WritingBanService writingBanService;
    private SessionService sessionService;
    private SecurityUtilsService securityUtilsService;
    private RoleService roleService;

    @Operation(security = @SecurityRequirement(name = "security"),
               summary = "Get all users", tags = { "System blocking users" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class)))) })
    @GetMapping(value = "/admin/list", produces = { "application/json" })
    public ResponseEntity<List<User>> allUsers() {
        List<User> userList = userService.findAll();
        return ResponseEntity.ok(userList);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Add user to blacklist", tags = { "System blocking users" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = BlackListDto.class)))})
    @PostMapping("/admin/blocking")
    public ResponseEntity <BlackListDto> blockUser(@RequestBody BlackListDto blackListDto) {
        User user = userService.findById(blackListDto.getId());
        BlackList list = blackListService.findByUser(user);
        BlackList blackList;
        roleService.getRoleByAuthority("ROLE_USER");
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
        return ResponseEntity.ok(blackListDto);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Add writingBan for user", tags = { "System blocking users" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = WritingBanDto.class)))})
    @PostMapping("/admin/writingBan")
    public ResponseEntity<WritingBanDto> writingBanUser(@RequestBody WritingBanDto writingBanDto) {
        User user = userService.findById(writingBanDto.getId());
        BanType type = BanType.valueOf(writingBanDto.getBanType());
        WritingBan oldWritingBan = writingBanService.getByUserAndType(user, type);
        WritingBan writingBan;
        LocalDateTime localDateTime;

        if (writingBanDto.getDateUnblock() == null || writingBanDto.getDateUnblock().equals("")) {
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
        return ResponseEntity.ok(writingBanDto);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
               summary = "Get all blocked users", tags = { "System blocking users" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BlackList.class))))})
    @GetMapping(value = "/admin/blackList", produces = { "application/json" })
    public ResponseEntity<List<BlackList>> allBlockedUsers() {
        List<BlackList> blackList = blackListService.findAll();
        return ResponseEntity.ok(blackList);
    }
}
