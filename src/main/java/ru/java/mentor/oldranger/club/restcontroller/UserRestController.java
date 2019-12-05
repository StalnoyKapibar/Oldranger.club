package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.user.InvitationService;
import ru.java.mentor.oldranger.club.service.user.UserService;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User")
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


    @Operation(security = @SecurityRequirement(name = "security"),
               summary = "Add user", tags = { "User" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = User.class))) })
    @PostMapping(produces = { "application/json" })
    public ResponseEntity<User> addUser(@RequestBody String key) {
        User user = new User(); //это "тестовый" юзер. Надо добавить получение данных юзера из формы
        user.setEmail("email@mail.ru");
        user.setNickName("Nick");
        userService.save(user);
        invitationService.getInvitationTokenByKey(key).setVisitor(user);
        invitationService.markAsUsed(key);
        return ResponseEntity.ok(user);
    }
}
