package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.UserStatisticDto;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.model.utils.EmailDraft;
import ru.java.mentor.oldranger.club.service.chat.MessageService;
import ru.java.mentor.oldranger.club.service.mail.EmailDraftService;
import ru.java.mentor.oldranger.club.service.mail.MailService;
import ru.java.mentor.oldranger.club.service.user.RoleService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@AllArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin")
public class AdminRestController {

    private UserStatisticService userStatisticService;
    private MessageService messageService;
    private EmailDraftService emailDraftService;
    private MailService mailService;
    private UserService userService;
    private SecurityUtilsService securityUtilsService;
    private RoleService roleService;

    @Operation(security = @SecurityRequirement(name = "security"),
               summary = "Get UserStatisticDto list", tags = { "Admin" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserStatisticDto.class)))) })
    @Parameter(in = ParameterIn.QUERY, name = "page",
            required = false, description = "номер страницы",
            allowEmptyValue = true,
            schema = @Schema(
                    type = "Integer",
                    example = "1"))
    @Parameter(in = ParameterIn.QUERY, name = "query",
            required = false, description = "значение для фильтрации только тех строк таблицы 'users', в которых данное значение " +
            "содержится хотя бы в одной из трех колонок: first_name, last_name или email",
            allowEmptyValue = true, allowReserved = true,
            schema = @Schema(
                    type = "string",
                    example = "my_email@gmail.com"))

    @GetMapping(value = "/users", produces = { "application/json" })
    public ResponseEntity<List<UserStatisticDto>> getAllUsers(@RequestParam(value = "page", required = false) Integer page,
                                                              @RequestParam(value = "query", required = false) String query) {
        if (page == null) page = 0;
        Pageable pageable = PageRequest.of(page, 5, Sort.by("user_id"));

        List<UserStatistic> users = userStatisticService.getAllUserStatistic(pageable).getContent();

        if (query != null && !query.trim().isEmpty()) {
            query = query.toLowerCase().trim().replaceAll("\\s++", ",");
            users = userStatisticService.getUserStatisticsByQuery(pageable, query).getContent();
        }

        List<UserStatisticDto> dtos = userStatisticService.getUserStatisticDtoFromUserStatistic(users);
        return ResponseEntity.ok(dtos);
    }


    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get Email Draft list", tags = { "Drafts" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmailDraft.class)))) })
    @GetMapping(value = "/getDrafts", produces = { "application/json" })
    public ResponseEntity<List<EmailDraft>> getDrafts(@Parameter(description="Page")
                                                          @RequestParam(value = "page", required = false) Integer page,
                              @PageableDefault(size = 5, sort = "lastEditDate", direction = Sort.Direction.DESC) Pageable pageable) {
        if (page != null) {
            pageable = PageRequest.of(page, 5, Sort.by("lastEditDate").descending());
        }
        List<EmailDraft> drafts = emailDraftService.getAllDraftsPageable(pageable).getContent();
        return ResponseEntity.ok(drafts);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get total pages number", description = "Total pages for email drafts", tags = { "Drafts" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Map.class))) })
    @GetMapping(value = "/getTotalPages", produces = { "application/json" })
    public ResponseEntity<Map<String,Integer>> getDrafts(@PageableDefault(size = 5) Pageable pageable) {
        Integer pages = emailDraftService.getAllDraftsPageable(pageable).getTotalPages();
        Map<String,Integer> totalPages = new HashMap<>();
        totalPages.put("totalPages", pages);
        return ResponseEntity.ok(totalPages);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Send mail to all users", tags = { "Direct Mail" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mail send",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Mail not send")})
    @PostMapping(value = "/sendMail", produces = { "application/json" })
    public ResponseEntity<String> sendMail(EmailDraft draft) {
        List<User> users = userService.findAll();
        List<String> mailList = new ArrayList<>();
        users.forEach(user -> mailList.add(user.getEmail()));
        String[] emails = (String[]) mailList.toArray();
        try {
            mailService.sendHtmlMessage(emails, draft);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get Email draft", description = "Get email draft by id", tags = { "Drafts" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = EmailDraft.class)))})
    @GetMapping(value = "/getDraft/{id}", produces = { "application/json" })
    public ResponseEntity<EmailDraft> editDraft(@PathVariable long id) {
        EmailDraft draft = emailDraftService.getById(id);
        return ResponseEntity.ok(draft);
    }


    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete draft", description = "Delete draft by id", tags = { "Drafts" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Draft deleted",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Deleting error")})
    @GetMapping(value = "/deleteDraft/{id}", produces = { "application/json" })
    public ResponseEntity<String> deleteDraft(@PathVariable long id) {
        try {
            emailDraftService.deleteDraft(id);
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Save draft", description = "Save draft", tags = { "Drafts" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Draft saved",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Saving error")})
    @PostMapping(value = "/saveDraft", produces = { "application/json" })
    public ResponseEntity<String> saveDraft(EmailDraft draft) {
        draft.setLastEditDate(LocalDateTime.now());
        try {
            emailDraftService.saveDraft(draft);
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "User edit", tags = {"Admin"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "User edit error")})
    @PutMapping(value = "/editUser/{userId}", produces = {"application/json"})
    public ResponseEntity<User> editUserById(@PathVariable Long userId,
                                             @RequestParam(value = "firstName") String firstName,
                                             @RequestParam(value = "lastName") String lastName,
                                             @RequestParam(value = "email") String email,
                                             @RequestParam(value = "nickName") String nickName,
                                             @RequestParam(value = "role") String role) {

        User user = userService.findById(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setNickName(nickName);
        user.setRole(roleService.getRoleByAuthority(role));

        if (userId == null || !securityUtilsService.isAdmin()) {
            return ResponseEntity.badRequest().build();
        }
        userService.save(user);
        return ResponseEntity.ok(user);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get user by id", tags = {"Admin"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found")})
    @GetMapping(value = "/getUser/{userId}", produces = {"application/json"})
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        User user = userService.findById(userId);

        if (userId == null || !securityUtilsService.isAdmin()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(user);
    }
}
