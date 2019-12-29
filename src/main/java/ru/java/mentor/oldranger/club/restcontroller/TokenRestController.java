package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.RegistrationUserDto;
import ru.java.mentor.oldranger.club.exceptions.passwords.PasswordException;
import ru.java.mentor.oldranger.club.model.user.InvitationToken;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.mail.MailService;
import ru.java.mentor.oldranger.club.service.user.InvitationService;
import ru.java.mentor.oldranger.club.service.user.RoleService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.PasswordsService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.Base64;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/token")
@Tag(name = "Registration token")
public class TokenRestController {

    private InvitationService invitationService;
    private UserService userService;
    private MailService mailService;
    private SecurityUtilsService securityUtilsService;
    private PasswordEncoder passwordEncoder;
    private PasswordsService passwordsService;
    private RoleService roleService;


    @Value("${server.protocol}")
    private String protocol;

    @Value("${server.name}")
    private String host;

    @Value("${server.port}")
    private String port;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Save invitation token", tags = { "Registration token" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitation key",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "User is not logged in")})
    @PostMapping(value = "/invite", produces = { "application/json" })
    public ResponseEntity<String> saveInvitationToken() {
        User user = securityUtilsService.getLoggedUser();
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        String key = invitationService.getCurrentKey(user);
        if (key == null) {
            key = invitationService.generateKey();
            InvitationToken invitationToken = new InvitationToken(key, user);
            invitationService.save(invitationToken);
        }
        return ResponseEntity.ok(key);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Send invite by mail", tags = { "Registration token" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status, 1 - success, 0 - failed",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "User is not logged in")})
    @PostMapping(value = "/invite/bymail", produces = { "application/json" })
    public ResponseEntity<String> sendInviteByMail(@Parameter(description="Email")
                                                   @RequestBody String mail) {
        User user = securityUtilsService.getLoggedUser();
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        String key = invitationService.generateKey();
        String link = protocol + "://" + host + ":" + port + "/invite?key=" + key;
        InvitationToken invitationToken = new InvitationToken(key, user, mail);
        String status = mailService.sendHtmlEmail(mail, user.getFirstName(), "letterToInvite.html", link);
        invitationService.markInviteOnMailAsUsed(mail);
        invitationService.save(invitationToken);
        return ResponseEntity.ok(status);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Send confirm by mail", tags = { "Registration token" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status, 1 - success, 0 - failed",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "User with this login or email already exists")})
    @PostMapping(value = "/confirm/bymail", produces = { "application/json" })
    public ResponseEntity<String> sendConfirmByMail(@Parameter(description = "New user data") @RequestBody RegistrationUserDto registrationUserDto) {
        if (userService.getUserByEmail(registrationUserDto.getEmail()) != null ||
                userService.getUserByNickName(registrationUserDto.getNickName()) != null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            passwordsService.checkStrength(registrationUserDto.getPassword());
        } catch (PasswordException e) {
            return ResponseEntity.badRequest().build();
        }
        registrationUserDto.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        String key = Base64.getEncoder().encodeToString(registrationUserDto.toString().getBytes());

        String mail = registrationUserDto.getEmail();
        String link = protocol + "://" + host + ":" + port + "/confirm?key=" + key;

        String status = mailService.sendHtmlEmail(mail, registrationUserDto.getNickName(), "letterToConfirm.html", link);

        return ResponseEntity.ok(status);
    }
}
