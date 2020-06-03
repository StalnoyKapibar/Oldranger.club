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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.EmailDraftDto;
import ru.java.mentor.oldranger.club.dto.ListUserStatisticDTO;
import ru.java.mentor.oldranger.club.dto.UserStatisticDto;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.EmailDraft;
import ru.java.mentor.oldranger.club.service.chat.MessageService;
import ru.java.mentor.oldranger.club.service.mail.EmailDraftService;
import ru.java.mentor.oldranger.club.service.mail.MailService;
import ru.java.mentor.oldranger.club.service.user.RoleService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;
import ru.java.mentor.oldranger.club.service.utils.WritingBanService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
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
    private WritingBanService writingBanService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get UserStatisticDto list", tags = {"Admin"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ListUserStatisticDTO.class))))})
    @Parameter(in = ParameterIn.QUERY, name = "page",
            required = false, description = "номер страницы (необязательный параметр)",
            allowEmptyValue = true,
            schema = @Schema(
                    type = "Integer",
                    example = "http://localhost:8888/api/admin/users?page=1"))
    @Parameter(in = ParameterIn.QUERY, name = "query",
            required = false, description = "значение для фильтрации  (необязательный параметр) только тех строк таблицы " +
            "'users', в которых данное значение " +
            "содержится хотя бы в одной из трех колонок: first_name, last_name или email",
            allowEmptyValue = true, allowReserved = true,
            schema = @Schema(
                    type = "string",
                    example = "http://localhost:8888/api/admin/users?query=moderator@javamentor.com  или http://localhost:8888/api/admin/users?query=Admin"))

    @GetMapping(value = "/users", produces = {"application/json"})
    public ResponseEntity<ListUserStatisticDTO> getAllUsers(@RequestParam(value = "page", required = false) Integer page,
                                                            @RequestParam(value = "query", required = false) String query) {

        List<UserStatisticDto> users;
        if (page == null) page = 0;
        Pageable pageable = PageRequest.of(page, 5, Sort.by("id"));
        if (query != null && !query.trim().isEmpty()) {
            query = query.toLowerCase().trim().replaceAll("\\s++", ",");
            users = userStatisticService.getUserStatisticsByQuery(pageable, query).getContent();
        } else {
            users = userStatisticService.getAllUserStatistic(pageable).getContent();
        }
        ListUserStatisticDTO listUserStatisticDTO = new ListUserStatisticDTO(users, userService.getCount());
        return ResponseEntity.ok(listUserStatisticDTO);
    }


    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get Email Draft list", tags = {"Drafts"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmailDraft.class))))})
    @GetMapping(value = "/getDrafts", produces = {"application/json"})
    public ResponseEntity<List<EmailDraft>> getDrafts(@Parameter(description = "Page")
                                                      @RequestParam(value = "page", required = false) Integer page,
                                                      @PageableDefault(size = 5, sort = "lastEditDate", direction = Sort.Direction.DESC) Pageable pageable) {
        if (page != null) {
            pageable = PageRequest.of(page, 5, Sort.by("lastEditDate").descending());
        }
        List<EmailDraft> drafts = emailDraftService.getAllDraftsPageable(pageable).getContent();
        return ResponseEntity.ok(drafts);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get total pages number", description = "Total pages for email drafts", tags = {"Drafts"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Map.class)))})
    @GetMapping(value = "/getTotalPages", produces = {"application/json"})
    public ResponseEntity<Map<String, Integer>> getDrafts(@PageableDefault(size = 5) Pageable pageable) {
        Integer pages = emailDraftService.getAllDraftsPageable(pageable).getTotalPages();
        Map<String, Integer> totalPages = new HashMap<>();
        totalPages.put("totalPages", pages);
        return ResponseEntity.ok(totalPages);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Send mail to all users", tags = {"Direct Mail"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mail send",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Mail not send")})
    @PostMapping(value = "/sendMail", produces = {"application/json"})
    public ResponseEntity<String> sendMail(@RequestBody EmailDraftDto draft) {

        String[] roles = draft.getRoles();
        String[] emails;
        List<User> users = userService.findAll();
        List<String> mailList = new ArrayList<>();
        if (roles.length == 1 && roles[0].equals("All")) {
            users.forEach(user -> mailList.add(user.getEmail()));
        } else {
            for (User user : users) {
                if (Arrays.stream(roles).anyMatch(role -> user.getRole().getAuthority().equals(role))) {
                    mailList.add(user.getEmail());
                }
            }
        }
        emails = mailList.toArray(new String[0]);
        try {
            mailService.sendHtmlMessage(emails, draft.getEmailDraft());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get Email draft", description = "Get email draft by id", tags = {"Drafts"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = EmailDraft.class)))})
    @GetMapping(value = "/getDraft/{id}", produces = {"application/json"})
    public ResponseEntity<EmailDraft> editDraft(@PathVariable long id) {
        EmailDraft draft = emailDraftService.getById(id);
        return ResponseEntity.ok(draft);
    }


    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete draft", description = "Delete draft by id", tags = {"Drafts"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Draft deleted",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Deleting error")})
    @GetMapping(value = "/deleteDraft/{id}", produces = {"application/json"})
    public ResponseEntity<String> deleteDraft(@PathVariable long id) {
        try {
            emailDraftService.deleteDraft(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Save draft", description = "Save draft", tags = {"Drafts"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Draft saved",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Saving error")})
    @PostMapping(value = "/saveDraft", produces = {"application/json"})
    public ResponseEntity<String> saveDraft(EmailDraft draft) {
        draft.setLastEditDate(LocalDateTime.now());
        try {
            emailDraftService.saveDraft(draft);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "User edit", tags = {"Admin"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "User edit error"),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @PutMapping(value = "/editUser/{userId}", produces = {"application/json"})
    public ResponseEntity<User> editUserById(@PathVariable Long userId,
                                             @RequestParam(value = "firstName") String firstName,
                                             @RequestParam(value = "lastName") String lastName,
                                             @RequestParam(value = "email") String email,
                                             @RequestParam(value = "nickName") String nickName,
                                             @RequestParam(value = "role") String role) {

        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!securityUtilsService.isAdmin()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.findById(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setNickName(nickName);
        user.setRole(roleService.getRoleByAuthority(role));

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
        if (userId == null || !securityUtilsService.isAdmin()) {
            return ResponseEntity.noContent().build();
        }
        User user = userService.findById(userId);
        user.setMute(writingBanService.getByUser(user).stream()
                .map(Enum::name).collect(Collectors.toList()));
        return ResponseEntity.ok(user);
    }
}
