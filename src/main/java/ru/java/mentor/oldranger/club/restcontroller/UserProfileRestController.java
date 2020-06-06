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
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.*;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.forum.TopicVisitAndSubscription;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserProfile;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.forum.TopicVisitAndSubscriptionService;
import ru.java.mentor.oldranger.club.service.user.InvitationService;
import ru.java.mentor.oldranger.club.service.user.UserProfileService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Tag(name = "User profile")
public class UserProfileRestController {

    private UserProfileService userProfileService;
    private UserStatisticService userStatisticService;
    private UserService userService;
    private InvitationService invitationService;
    private TopicService topicService;
    private TopicVisitAndSubscriptionService topicVisitAndSubscriptionService;
    private CommentService commentService;
    private PasswordEncoder passwordEncoder;
    private SecurityUtilsService securityUtilsService;
    private CacheManager cacheManager;


    @InitBinder
    // передаем пустые строки как null
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get ProfileDto of logged in user", tags = {"User profile"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ProfileDto.class))),
            @ApiResponse(responseCode = "204", description = "User is not logged in")})
    @GetMapping(value = "/profile", produces = {"application/json"})
    public ResponseEntity<ProfileDto> getProfile() {
        User user = securityUtilsService.getLoggedUser();
        if (user == null) {
            return ResponseEntity.noContent().build();

        }
        UserProfile profile = userProfileService.getUserProfileByUser(user);
        UserStatistic stat = userStatisticService.getUserStaticByUser(user);
        ProfileDto dto = userProfileService.buildProfileDto(profile, stat, true, securityUtilsService.isLoggedUserIsUser());

        return ResponseEntity.ok(dto);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get ProfileDto of another user by id", tags = {"User profile"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ProfileDto.class))),
            @ApiResponse(responseCode = "404", description = "User by id not found")})
    @GetMapping(value = "/{id}", produces = {"application/json"})
    public ResponseEntity<?> getAnotherUserProfile(@PathVariable Long id) {
        User user;
        try {
            user = userService.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        UserProfile profile = userProfileService.getUserProfileByUser(user);
        UserStatistic stat = userStatisticService.getUserStaticByUser(user);
        ProfileDto dto = userProfileService.buildProfileDto(profile, stat, false, securityUtilsService.isLoggedUserIsUser());

        return ResponseEntity.ok(dto);
    }

    //TODO ErrorDto - зачем?
    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Update profile", tags = {"User profile"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = UpdateProfileDto.class))),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @PostMapping("/updateProfile")
    public ResponseEntity<User> updateProfile(@RequestBody UpdateProfileDto updateProfileDto) {
        User currentUser = securityUtilsService.getLoggedUser();
        String currentNickName = currentUser.getNickName();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserProfile profile = userProfileService.getUserProfileByUser(currentUser);
        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(currentUser);
        }
        userProfileService.updateUserProfile(profile, updateProfileDto);
        User user = userService.updateUser(currentUser, updateProfileDto);

        if(!currentNickName.equals(user.getNickName())) {
            Objects.requireNonNull(cacheManager.getCache("user")).evict(currentNickName);
        }
        return ResponseEntity.ok(user);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get CommentDto list for current user", tags = {"User profile"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = CommentDto.class))),
            @ApiResponse(responseCode = "401", description = "User is not logged in")})
    @GetMapping(value = "/comments", produces = {"application/json"})
    public ResponseEntity<List<CommentDto>> getComments(
            @RequestAttribute(value = "page", required = false) Integer page) {
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (page == null) {
            page = 0;
        }
        Pageable pageable = PageRequest.of(page, 10, Sort.by("dateTime").descending());
        List<CommentDto> dtos = commentService.getPageableCommentDtoByUser(currentUser, pageable).getContent();

        return ResponseEntity.ok(dtos);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get subscriptions for current user", tags = {"User profile"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = TopicVisitAndSubscription.class))),
            @ApiResponse(responseCode = "401", description = "User is not logged in")})
    @GetMapping(value = "/subscriptions", produces = {"application/json"})
    public ResponseEntity<List<Topic>> getSubscriptions(
            @Parameter(description = "Page") @RequestParam(value = "page") Integer page,
            @PageableDefault(size = 10) @RequestParam(required = false) Pageable pageable) {
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (page == null) {
            page = 0;
        } else {
            page = page - 1;
        }
        pageable = PageRequest.of(page, 10, Sort.by("lastMessageTime"));

        List<Topic> topics = topicVisitAndSubscriptionService.getPagebleSubscribedTopicsForUser(currentUser, pageable)
                .getContent();
        return ResponseEntity.ok(topics);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Add topic to subscriptions", tags = {"User profile"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = TopicVisitAndSubscription.class))),
            @ApiResponse(responseCode = "204", description = "Error adding topic to subscription"),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @PostMapping(value = "/subscriptions", produces = {"application/json"})
    public ResponseEntity<TopicVisitAndSubscription> addTopicToSubscription(@RequestParam(value = "topicId", required = true) Long topicId) {
        User user = securityUtilsService.getLoggedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Topic topic = topicService.findById(topicId);
        if (topic == null) {
            return ResponseEntity.status(204).build();
        }
        TopicVisitAndSubscription topicVisitAndSubscription = topicVisitAndSubscriptionService.getByUserAndTopic(user, topic);
        if ((topicVisitAndSubscription != null) && (topicVisitAndSubscription.isSubscribed())) {
            return ResponseEntity.status(204).build();
        } else {
            topicVisitAndSubscription = topicVisitAndSubscriptionService.subscribeUserOnTopic(user, topic);
            return ResponseEntity.ok(topicVisitAndSubscription);
        }
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete topic from subscription", description = "Delete topic from subscription", tags = {"User profile"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete successful"),
            @ApiResponse(responseCode = "204", description = "Error delete topic from subscription"),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @DeleteMapping("/subscriptions")
    public ResponseEntity deleteTopicFromSubscription(@RequestParam(value = "topicId", required = true) Long topicId) {
        User user = securityUtilsService.getLoggedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Topic topic = topicService.findById(topicId);
        if (topic == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        TopicVisitAndSubscription topicVisitAndSubscription = topicVisitAndSubscriptionService.getByUserAndTopic(user, topic);
        if (topicVisitAndSubscription == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        if (!topicVisitAndSubscription.isSubscribed()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        topicVisitAndSubscriptionService.unsubscribe(topicVisitAndSubscription);

        return ResponseEntity.ok().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all topics started by current user", tags = {"User profile"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Topic.class))),
            @ApiResponse(responseCode = "401", description = "User is not logged in")})
    @GetMapping(value = "/topics", produces = {"application/json"})
    public ResponseEntity<List<Topic>> getTopics(@RequestParam(value = "page", required = false) Integer page) {
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (page == null) {
            page = 0;
        } else {
            page = page - 1;
        }
        Pageable pageable = PageRequest.of(page, 10, Sort.by("lastMessageTime"));
        List<Topic> dtos = topicService.findAllTopicsStartedByUser(currentUser, pageable).getContent();

        return ResponseEntity.ok(dtos);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get InviteDto for current user", tags = {"User profile"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = InviteDto.class))),
            @ApiResponse(responseCode = "401", description = "User is not logged in or does not have enough rights")})
    @GetMapping(value = "/invite", produces = {"application/json"})
    public ResponseEntity<InviteDto> getInvitation() {
        User currentUser = securityUtilsService.getLoggedUser();
        Boolean isUser = securityUtilsService.isLoggedUserIsUser();
        if (currentUser == null || !isUser) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String key = invitationService.getCurrentKey(currentUser);
        InviteDto dto = new InviteDto(currentUser, key);

        return ResponseEntity.ok(dto);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Change password", tags = {"User profile"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "User is not logged in")})
    @PostMapping(value = "/changePassword", produces = {"application/json"})
    public ResponseEntity<ErrorDto> changePassword(@RequestParam String oldPass,
                                                   @RequestParam String newPass,
                                                   @RequestParam String passConfirm) {
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (passwordEncoder.matches(oldPass, currentUser.getPassword())) {
            if (passConfirm.equals(newPass)) {
                currentUser.setPassword(passwordEncoder.encode(newPass));
                userService.save(currentUser);
                return ResponseEntity.ok(new ErrorDto("Пароль обновлен"));
            }
        }

        return ResponseEntity.ok(new ErrorDto("Пароль указан неверно"));
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get ID of currently logged user", tags = {"User profile"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "401", description = "User is not logged in")})
    @GetMapping(value = "/getloggeduserid", produces = {"application/json"})
    public ResponseEntity<Long> getCurrentUserId() {
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(currentUser.getId());
    }

}
