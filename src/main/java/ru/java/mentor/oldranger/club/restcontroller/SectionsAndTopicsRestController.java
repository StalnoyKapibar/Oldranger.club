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
import ru.java.mentor.oldranger.club.model.utils.BanType;
import ru.java.mentor.oldranger.club.service.forum.SectionsAndTopicsService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
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
            tags = { "Sections and topics" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TopicAndNewMessagesCountDto.class)))) })
    @GetMapping(value = "/actualtopics", produces = { "application/json" })
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
    @PostMapping(value = "/topic/new", produces = {"application/json"})
    public ResponseEntity<Topic> getSectionsAndTopicsDto(@RequestBody Topic topicDetails) {

        Topic topic = new Topic();

        topic.setName(topicDetails.getName());
        topic.setTopicStarter(securityUtilsService.getLoggedUser());
        topic.setStartTime(LocalDateTime.now());
        topic.setLastMessageTime(LocalDateTime.now());
        topic.setSubsection(topicDetails.getSubsection());
        topic.setStartMessage(topicDetails.getStartMessage());

        topic.setHideToAnon(topic.getSubsection().isHideToAnon() | topicDetails.isHideToAnon());

        topicService.createTopic(topic);

        if (topic.getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(topic);
    }
}
