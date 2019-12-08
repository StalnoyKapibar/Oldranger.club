package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.model.user.InvitationToken;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.mail.MailService;
import ru.java.mentor.oldranger.club.service.user.InvitationService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

@RestController
@RequestMapping("/api/invite")
@Tag(name = "Invitation token")
public class InvitationTokenRestController {

    private InvitationService invitationService;
    private UserService userService;
    private MailService mailService;
    private SecurityUtilsService securityUtilsService;

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

    @Autowired
    public void setSecurityUtilsService(SecurityUtilsService service) {
        this.securityUtilsService = service;
    }

    @Operation(security = @SecurityRequirement(name = "security"),
               summary = "Save invitation token", tags = { "Invitation token" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitation key",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "204", description = "User is not logged in")})
    @PostMapping(produces = { "application/json" })
    public ResponseEntity<String> saveInvitationToken() {
        User user = securityUtilsService.getLoggedUser();
        if (user == null) return ResponseEntity.noContent().build();

        String key = invitationService.getCurrentKey(user);
        if (key == null) {
            key = invitationService.generateKey();
            InvitationToken invitationToken = new InvitationToken(key, user);
            invitationService.save(invitationToken);
        }
        return ResponseEntity.ok(key);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
               summary = "Send invite by mail", tags = { "Invitation token" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status, 1 - success, 0 - failed",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "204", description = "User is not logged in")})
    @PostMapping(value = "/bymail", produces = { "application/json" }, consumes = { "application/json" })
    public ResponseEntity<String> sendInviteByMail(@Parameter(description="Email")
                                                   @RequestBody String mail) {
        User user = securityUtilsService.getLoggedUser();
        if (user == null) return ResponseEntity.noContent().build();

        String key = invitationService.generateKey();
        String link = protocol + "://" + host + ":" + port + "/invite?key=" + key;
        InvitationToken invitationToken = new InvitationToken(key, user, mail);
        String status = mailService.sendHtmlEmail(mail, user.getFirstName(), "letter.html", link);
        invitationService.markInviteOnMailAsUsed(mail);
        invitationService.save(invitationToken);
        return ResponseEntity.ok(status);
    }
}
