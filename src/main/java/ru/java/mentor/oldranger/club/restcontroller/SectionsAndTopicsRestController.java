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
import ru.java.mentor.oldranger.club.dto.SectionsAndTopicsDto;
import ru.java.mentor.oldranger.club.dto.TopicAndNewMessagesCountDto;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BanType;
import ru.java.mentor.oldranger.club.service.forum.SectionsAndTopicsService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.user.RoleService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;
import ru.java.mentor.oldranger.club.service.utils.WritingBanService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3030", maxAge = 3600)
@Tag(name = "Sections and topics")
public class SectionsAndTopicsRestController {

    private SecurityUtilsService securityUtilsService;
    private TopicService topicService;
    private SectionsAndTopicsService sectionsAndTopicsService;
    private WritingBanService writingBanService;
    private RoleService roleService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get SectionsAndTopicsDto list", description = "limit 10", tags = {"Sections and topics"})
    @Deprecated
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SectionsAndTopicsDto.class)))),
            @ApiResponse(responseCode = "204", description = "invalid topic id")})
    @GetMapping(value = "/sectionsandactualtopics", produces = {"application/json"})
    public ResponseEntity<List<SectionsAndTopicsDto>> getSectionsAndTopicsDto() {
        List<SectionsAndTopicsDto> dtos = sectionsAndTopicsService.getAllSectionsAndActualTopicsLimit10BySection();

        return ResponseEntity.ok(dtos);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get TopicAndNewMessagesCountDto list",
            description = "Get actual topics, limit topics in section: 10",
            tags = {"Sections and topics"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TopicAndNewMessagesCountDto.class))))})
    @GetMapping(value = "/actualtopics", produces = {"application/json"})
    public ResponseEntity<List<TopicAndNewMessagesCountDto>> getMostPopularTopics() {
        List<TopicAndNewMessagesCountDto> dtos = topicService.getTopicsDto(topicService.getActualTopicsLimit10());
        return ResponseEntity.ok(dtos);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Is it forbidden to create new topics", tags = {"Sections and topics"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Boolean",
                    content = @Content(schema = @Schema(implementation = Boolean.class)))})
    @GetMapping("/isForbidden")
    ResponseEntity<Boolean> isForbidden() {
        boolean isForbidden = writingBanService.isForbidden(securityUtilsService.getLoggedUser(), BanType.ON_FORUM_MESS);
        return ResponseEntity.ok(isForbidden);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Creates a new topic", tags = {"Sections and topics"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Topic created",
                    content = @Content(schema = @Schema(implementation = Topic.class))),
            @ApiResponse(responseCode = "400", description = "Failed to create topic")})
    @PostMapping(value = "/topic/new", consumes = {"application/json"})
    public ResponseEntity<Topic> getSectionsAndTopicsDto(@RequestBody Topic topicDetails) {

        Topic topic = new Topic();

        topic.setName(topicDetails.getName());
        topic.setTopicStarter(securityUtilsService.getLoggedUser());
        topic.setStartTime(LocalDateTime.now());
        topic.setLastMessageTime(LocalDateTime.now());
        topic.setSubsection(topicDetails.getSubsection());
        topic.setStartMessage(topicDetails.getStartMessage());

        topic.setHideToAnon(topic.getSubsection().isHideToAnon() | topicDetails.isHideToAnon());
        topic.setForbidComment(false);

        topicService.createTopic(topic);

        if (topic.getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(topic);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Edit topic", tags = {"Sections and topics"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Topic edited",
                    content = @Content(schema = @Schema(implementation = Topic.class))),
            @ApiResponse(responseCode = "400", description = "Error editing topic")})
    @PutMapping(value = "/topic/edit", produces = {"application/json"})
    public ResponseEntity<Topic> editTopic(@RequestBody Topic topicDetails) {

        Topic topic = topicService.findById(topicDetails.getId());
        boolean moderator = securityUtilsService.isAuthorityReachableForLoggedUser(roleService.getRoleByAuthority("ROLE_MODERATOR"));
        User user = topicDetails.getTopicStarter();
        User currentUser = securityUtilsService.getLoggedUser();

        topic.setStartMessage(topicDetails.getStartMessage());
        topic.setSubsection(topicDetails.getSubsection());
        topic.setName(topicDetails.getName());
        topic.setHideToAnon(topic.getSubsection().isHideToAnon() | topicDetails.isHideToAnon());

        if (securityUtilsService.isModerator()) {
            topic.setForbidComment(topicDetails.isForbidComment());
        } else {
            topic.setForbidComment(false);
        }

        if (topicDetails.getId() == null || !currentUser.getId().equals(user.getId()) && !securityUtilsService.isModerator()) {
            return ResponseEntity.badRequest().build();
        }
        topicService.editTopicByName(topic);
        return ResponseEntity.ok(topic);
    }
}
