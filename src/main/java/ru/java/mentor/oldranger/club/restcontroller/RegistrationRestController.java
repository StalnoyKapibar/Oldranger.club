package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.RequestRegistrationDto;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.mail.MailService;
import ru.java.mentor.oldranger.club.service.user.InvitationService;
import ru.java.mentor.oldranger.club.service.user.RoleService;
import ru.java.mentor.oldranger.club.service.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/registration")
@Tag(name = "Registration user")
public class RegistrationRestController {
    private InvitationService invitationService;
    private UserService userService;
    private RoleService roleService;
    private MailService mailService;


    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Add user", tags = {"Registration user"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "request on invitation has been successfully saved",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400",
                    description = "parameter 'key' not correct")})
    @PostMapping
    public ResponseEntity<String> addUser(@Parameter(description = "parameter 'key' from url") @RequestParam String key) {
        String enc = key.replaceAll("%2F", "/").replaceAll("%3F", "?")
                .replaceAll("%3D", "=").replaceAll("%26", "&")
                .replaceAll("%2523", "#").replaceAll(" ", "+");
        String dec = null;
        User user = null;
        try {
            dec = new String(Base64.getDecoder().decode(enc));
            user = userService.getUserByInviteKey(dec.split(" ")[5]);
            user.setNickName(dec.split(" ")[0]);
            user.setFirstName(dec.split(" ")[1]);
            user.setLastName(dec.split(" ")[2]);
            user.setEmail(dec.split(" ")[3]);
            user.setPassword(dec.split(" ")[4]);
            user.setRole(roleService.getRoleByAuthority("ROLE_PROSPECT"));
            user.setRegDate(LocalDateTime.now());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        userService.save(user);
        invitationService.getInvitationTokenByKey(dec.split(" ")[5]).setVisitor(user);
        invitationService.markAsUsed(dec.split(" ")[5]);
        return ResponseEntity.ok("Ok");
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Registration request", tags = {"Registration user"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "request on registration has been successfully sent",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400",
                    description = "these email is already used")})
    @PostMapping(value = "/new", consumes = {"application/json"})
    public ResponseEntity<String> sendRequestToAdmin(@RequestBody RequestRegistrationDto registrationUserDto) {
        String email = registrationUserDto.getEmail();
        if (userService.getUserByEmail(email) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            List<Role> roles = new ArrayList<>();
            roles.add(roleService.getRoleByAuthority("ROLE_ADMIN"));
            roles.add(roleService.getRoleByAuthority("ROLE_MODERATOR"));
            String status = mailService.sendMessageByRoles(roles, registrationUserDto);
            return ResponseEntity.ok(status);
        }
    }
}