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
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BlackList;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.BlackListService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Tag(name = "System blocking users")
public class SystemBlockingUsersRestController {

    private UserService userService;
    private BlackListService blackListService;

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
               summary = "Get all blocked users", tags = { "System blocking users" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BlackList.class)))) })
    @GetMapping(value = "/admin/blackList", produces = { "application/json" })
    public ResponseEntity<List<BlackList>> allBlockedUsers() {
        List<BlackList> blackList = blackListService.findAll();
        return ResponseEntity.ok(blackList);
    }
}
