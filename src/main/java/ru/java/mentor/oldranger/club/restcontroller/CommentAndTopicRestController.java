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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.dto.CommentDto;
import ru.java.mentor.oldranger.club.dto.TopicAndCommentsDTO;
import ru.java.mentor.oldranger.club.model.forum.Comment;
import ru.java.mentor.oldranger.club.model.forum.ImageComment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.dto.CommentCreateAndUpdateDto;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.forum.ImageCommnetService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.forum.TopicVisitAndSubscriptionService;
import ru.java.mentor.oldranger.club.service.user.RoleService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Tag(name = "Topic and comments")
public class CommentAndTopicRestController {

    private CommentService commentService;
    private TopicService topicService;
    private TopicVisitAndSubscriptionService topicVisitAndSubscriptionService;
    private SecurityUtilsService securityUtilsService;
    private UserService userService;
    private RoleService roleService;
    private ImageCommnetService imageCommnetService;


    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get a topic and a list of comments DTO", description = "Get a topic and a list of comments for this topic by topic id", tags = {"Topic and comments"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TopicAndCommentsDTO.class)))),
            @ApiResponse(responseCode = "204", description = "invalid topic id")})
    @GetMapping(value = "/topic/{topicId}", produces = {"application/json"})
    public ResponseEntity<TopicAndCommentsDTO> getTopicAndPageableComments(@PathVariable(value = "topicId") Long topicId,
                                                                           @RequestParam(value = "page", required = false) Integer page,
                                                                           @RequestParam(value = "pos", required = false) Integer position,
                                                                           @RequestParam(value = "limit", required = false) Integer limit) {

        User currentUser = securityUtilsService.getLoggedUser();
        Topic topic = topicService.findById(topicId);
        if (topic == null) {
            return ResponseEntity.noContent().build();
        }

        if (currentUser == null && (topic.isHideToAnon() || topic.getSubsection().isHideToAnon())) {
            return ResponseEntity.badRequest().build();
        }

        if (limit == null) {
            limit = 10;
        }

        if (page == null) page = 0;
        Pageable pageable = PageRequest.of(page, limit, Sort.by("dateTime"));

        if (position == null) {
            position = 0;
        }

        Page<CommentDto> dtos = commentService.getPageableCommentDtoByTopic(topic, pageable, position);
        TopicAndCommentsDTO topicAndCommentsDTO = new TopicAndCommentsDTO(topic, dtos);
        topicVisitAndSubscriptionService.updateVisitTime(currentUser, topic);

        return ResponseEntity.ok(topicAndCommentsDTO);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get Topic ", description = "Get topic by topic id", tags = {"Topic and comments"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Topic.class)))),
            @ApiResponse(responseCode = "204", description = "invalid topic id")})
    @GetMapping(value = "/getTopic/{topicId}", produces = {"application/json"})
    public ResponseEntity<Topic> getTopicById(@PathVariable(value = "topicId") Long topicId) {
        Topic topic = topicService.findById(topicId);
        if (topic == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(topic);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Add a comment on topic", tags = {"Topic and comments"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = CommentDto.class))),
            @ApiResponse(responseCode = "400",
                   description = "Error adding comment")})
    @PostMapping(value = "/comment/add", consumes = {"multipart/form-data"})
    public ResponseEntity<CommentDto> addMessageOnTopic(@ModelAttribute @Valid CommentCreateAndUpdateDto messageComentsEntity,
                                                        @RequestPart(required = false) MultipartFile image1,
                                                        @RequestPart(required = false) MultipartFile image2

                                                        ) {
        Comment comment;
        User currentUser = securityUtilsService.getLoggedUser();
        Topic topic = topicService.findById(messageComentsEntity.getIdTopic());
        User user = userService.findById(messageComentsEntity.getIdUser());
        LocalDateTime localDateTime = LocalDateTime.now();
        if (messageComentsEntity.getAnswerID() != 0) {
            Comment answer = commentService.getCommentById(messageComentsEntity.getAnswerID());
            comment = new Comment(topic, user, answer, localDateTime, messageComentsEntity.getText());
        } else {
            comment = new Comment(topic, user, null, localDateTime, messageComentsEntity.getText());
        }

        if (topic.isForbidComment() || user.getId() == null && !currentUser.getId().equals(user.getId())) {
            return ResponseEntity.badRequest().build();
        }

        if (image1 != null) {
            ImageComment imageComment = imageCommnetService.createNewImage(image1);
            imageComment.setComment(comment);
            imageCommnetService.save(imageComment);
        }

        if (image2 != null) {
            ImageComment imageComment = imageCommnetService.createNewImage(image2);
            imageComment.setComment(comment);
            imageCommnetService.save(imageComment);
        }
        commentService.createComment(comment);
        CommentDto commentDto = commentService.assembleCommentDto(comment);
        return ResponseEntity.ok(commentDto);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete comment from topic", description = "Delete comment by id", tags = {"Topic and comments"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment deleted"),
            @ApiResponse(responseCode = "404", description = "Error deleting comment")})
    @DeleteMapping(value = "/comment/delete/{commentId}", produces = {"application/json"})
    public ResponseEntity<CommentDto> deleteComment(@PathVariable(value = "commentId") Long id) {
        Comment comment = commentService.getCommentById(id);
        boolean admin = securityUtilsService.isAuthorityReachableForLoggedUser(roleService.getRoleByAuthority("ROLE_ADMIN"));
        boolean moderator = securityUtilsService.isAuthorityReachableForLoggedUser(roleService.getRoleByAuthority("ROLE_MODERATOR"));
        User currentUser = securityUtilsService.getLoggedUser();
        User user = comment.getUser();

        if (comment.getId() == null || !currentUser.getId().equals(user.getId()) && !admin && !moderator) {
            return ResponseEntity.notFound().build();
        }
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Update a comment", tags = {"Topic and comments"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = CommentDto.class))),
            @ApiResponse(responseCode = "400", description = "Error updating comment")})
    @PutMapping(value = "/comment/update", produces = {"multipart/form-data"})
    public ResponseEntity<CommentDto> updateComment(@RequestParam(value = "commentID") Long commentID,
                                                    @RequestPart(required = false) MultipartFile image1,
                                                    @RequestPart(required = false) MultipartFile image2,
                                                    @RequestPart CommentCreateAndUpdateDto messageComments) {

        Comment comment = commentService.getCommentById(commentID);
        Topic topic = topicService.findById(messageComments.getIdTopic());
        User currentUser = securityUtilsService.getLoggedUser();
        User user = comment.getUser();
        List<ImageComment> images = new ArrayList<>();
        boolean admin = securityUtilsService.isAuthorityReachableForLoggedUser(roleService.getRoleByAuthority("ROLE_ADMIN"));
        boolean moderator = securityUtilsService.isAuthorityReachableForLoggedUser(roleService.getRoleByAuthority("ROLE_MODERATOR"));
        boolean allowedEditingTime = LocalDateTime.now().compareTo(comment.getDateTime().plusDays(7)) >= 0;

        comment.setTopic(topicService.findById(messageComments.getIdTopic()));
        comment.setCommentText(messageComments.getText());
        if (messageComments.getAnswerID() != 0) {
            comment.setAnswerTo(commentService.getCommentById(messageComments.getAnswerID()));
        } else {
            comment.setAnswerTo(null);
        }
        comment.setDateTime(comment.getDateTime());


        if (messageComments.getIdUser() == null || topic.isForbidComment() || !currentUser.getId().equals(user.getId()) && !admin && !moderator || !admin && !moderator && !allowedEditingTime) {
            return ResponseEntity.badRequest().build();
        }
        commentService.updateComment(comment);

        if (image1 != null) {
            ImageComment imageComment = imageCommnetService.createNewImage(image1);
            imageComment.setComment(comment);
            imageCommnetService.save(imageComment);
        }

        if (image2 != null) {
            ImageComment imageComment = imageCommnetService.createNewImage(image2);
            imageComment.setComment(comment);
            imageCommnetService.save(imageComment);
        }
        CommentDto commentDto = commentService.assembleCommentDto(comment);
        return ResponseEntity.ok(commentDto);
    }
}
