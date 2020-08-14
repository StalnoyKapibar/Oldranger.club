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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.dto.SectionsAndTopicsDto;
import ru.java.mentor.oldranger.club.dto.TopicAndNewMessagesCountDto;
import ru.java.mentor.oldranger.club.model.comment.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BanType;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.forum.SectionsAndTopicsService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.media.MediaService;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;
import ru.java.mentor.oldranger.club.service.utils.WritingBanService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    private PhotoService photoService;
    private MediaService mediaService;
    private PhotoAlbumService albumService;
    private CommentService commentService;

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
        List<TopicAndNewMessagesCountDto> dtos = null;
        User user = securityUtilsService.getLoggedUser();
        if (user == null) {
            dtos = topicService.getTopicsDto(topicService.getActualTopicsLimit10BySectionForAnon());
        } else {
            dtos = topicService.getTopicsDto(topicService.getActualTopicsLimit10());
        }
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
            @ApiResponse(responseCode = "400", description = "Failed to create topic"),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @PostMapping(value = "/topic/new", produces = {"application/json"}, consumes = {"multipart/form-data"})
    public ResponseEntity<Topic> getSectionsAndTopicsDto(@ModelAttribute @Valid Topic topicDetails,
                                                         @RequestParam List<MultipartFile> photos) {
        User user = securityUtilsService.getLoggedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Topic topic = new Topic();
        PhotoAlbum photoAlbum = new PhotoAlbum("PhotoAlbum by " + topicDetails.getName());
        photoAlbum.setMedia(mediaService.findMediaByUser(user));
        photoAlbum.setAllowView(true);
        albumService.save(photoAlbum);

        for (MultipartFile file : photos) {
            photoService.save(photoAlbum, file, 0);
        }

        topic.setName(topicDetails.getName());
        topic.setTopicStarter(securityUtilsService.getLoggedUser());
        topic.setStartTime(LocalDateTime.now());
        topic.setLastMessageTime(LocalDateTime.now());
        topic.setSubsection(topicDetails.getSubsection());
        topic.setStartMessage(topicDetails.getStartMessage());
        topic.setPhotoAlbum(photoAlbum);

        topic.setHideToAnon(topicDetails.isHideToAnon() || topic.getSubsection().isHideToAnon());
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
            @ApiResponse(responseCode = "400", description = "Error editing topic"),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @PutMapping(value = "/topic/edit", produces = {"application/json"})
    public ResponseEntity<Topic> editTopic(@RequestBody Map<String, Object> param) {

        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null || !securityUtilsService.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Integer id = (Integer) param.get("id");
        String name = (String) param.get("name");
        String startMessage = (String) param.get("startMessage");
        Boolean hideToAnon = (Boolean) param.get("hideToAnon");
        Boolean forbidComment = (Boolean) param.get("forbidComment");

        Topic topic = topicService.findById(Long.valueOf(id));
        topic.setStartMessage(startMessage);
        topic.setSubsection(topic.getSubsection());
        topic.setName(name);
        topic.setHideToAnon(topic.getSubsection().isHideToAnon() | hideToAnon);

        if (securityUtilsService.isModerator()) {
            topic.setForbidComment(forbidComment);
        } else {
            topic.setForbidComment(false);
        }

        topicService.editTopicByName(topic);
        return ResponseEntity.ok(topic);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete topic", tags = {"Sections and topics"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Topic deleted",
                    content = @Content(schema = @Schema(implementation = Topic.class))),
            @ApiResponse(responseCode = "400", description = "Error by deleting topic"),
            @ApiResponse(responseCode = "401", description = "User have no authority"),
            @ApiResponse(responseCode = "404", description = "There is no topic with such id")})
    @DeleteMapping(value = "/topic/delete", produces = {"application/json"})
    public ResponseEntity<Topic> deleteTopic(@RequestParam Long id) {
        User user = securityUtilsService.getLoggedUser();
        if (topicService.findById(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User userStarter = topicService.findById(id).getTopicStarter();
        if (securityUtilsService.isModerator() || userStarter.getId().equals(user.getId())) {
            topicService.deleteTopicById(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
