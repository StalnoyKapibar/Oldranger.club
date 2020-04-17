package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import ru.java.mentor.oldranger.club.dto.RoleDTO;
import ru.java.mentor.oldranger.club.dto.SectionsAndTopicsDto;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.EmailDraft;
import ru.java.mentor.oldranger.club.service.forum.SectionsAndTopicsService;
import ru.java.mentor.oldranger.club.service.mail.MailService;
import ru.java.mentor.oldranger.club.service.user.RoleService;
import ru.java.mentor.oldranger.club.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Tag(name = "Role group")
public class RoleRestController {

    private RoleService roleService;
    private UserService userService;
    private MailService mailService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get Role list",
            description = "Get Role all",
            tags = {"Role all"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SectionsAndTopicsDto.class))))})
    @GetMapping(value = "/group/all", produces = {"application/json"})
    public ResponseEntity<List<Role>> allGroup() {
        return ResponseEntity.ok(roleService.getAllRole());
    }


    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Send mail to Group", tags = { "Direct Mail" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mail send",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Mail not send")})
    @PostMapping(value = "/group/sendMail", produces = { "application/json" })
    public ResponseEntity<String> sendMail(@RequestParam(value = "roleId") Long roleId,
                                           @ModelAttribute("emailDraft") EmailDraft draft){
        List<User> users = userService.findAll();
        List<String> mailList = new ArrayList<>();

        users.stream().filter(user-> user.getRole().getId().equals(roleId)).forEach(user-> mailList.add(user.getEmail()));

        String[] emails = (String[]) mailList.toArray();
        try{
            mailService.sendHtmlMessage(emails, draft);
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

}
