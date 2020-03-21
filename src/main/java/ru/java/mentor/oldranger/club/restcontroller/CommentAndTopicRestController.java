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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.dto.CommentCreateAndUpdateDto;
import ru.java.mentor.oldranger.club.dto.CommentDto;
import ru.java.mentor.oldranger.club.dto.TopicAndCommentsDTO;
import ru.java.mentor.oldranger.club.model.comment.Comment;
import ru.java.mentor.oldranger.club.model.comment.PhotoComment;
import ru.java.mentor.oldranger.club.model.forum.ImageComment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.forum.TopicVisitAndSubscriptionService;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.CheckFileTypeService;
import ru.java.mentor.oldranger.club.service.utils.FilterHtmlService;
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

    private final CommentService commentService;
    private final TopicService topicService;
    private final TopicVisitAndSubscriptionService topicVisitAndSubscriptionService;
    private final SecurityUtilsService securityUtilsService;
    private final UserService userService;
    private final CheckFileTypeService checkFileTypeService;
    private final PhotoAlbumService photoAlbumService;
    private final PhotoService photoService;
    private final FilterHtmlService filterHtmlService;

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

        Page<CommentDto> dtos = commentService.getPageableCommentDtoByTopic(topic, pageable, position, currentUser);
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
    public ResponseEntity<CommentDto> addMessageOnTopic(@ModelAttribute @Valid CommentCreateAndUpdateDto messageCommentEntity,
                                                        @RequestPart(required = false) MultipartFile image1,
                                                        @RequestPart(required = false) MultipartFile image2) {
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        String cleanedText = filterHtmlService.filterHtml(messageCommentEntity.getText());
        if(commentService.isEmptyComment(cleanedText)){
            return ResponseEntity.badRequest().build();
        }

        Topic topic = topicService.findById(messageCommentEntity.getIdTopic());
        User user = userService.findById(messageCommentEntity.getIdUser());
        boolean checkFirstImage = checkFileTypeService.isValidImageFile(image1);
        boolean checkSecondImage = checkFileTypeService.isValidImageFile(image2);
        if (topic.isForbidComment() || user.getId() == null && !currentUser.getId().equals(user.getId()) || !checkFirstImage || !checkSecondImage) {
            return ResponseEntity.badRequest().build();
        }

        Comment comment;
        LocalDateTime localDateTime = LocalDateTime.now();
        if (messageCommentEntity.getAnswerID() != null) {
            Comment answer = commentService.getCommentById(messageCommentEntity.getAnswerID());
            comment = new Comment(topic, user, answer, localDateTime, cleanedText);
        } else {
            comment = new Comment(topic, user, null, localDateTime, cleanedText);
        }
        commentService.createComment(comment);

        if (image1 != null) {
            photoService.save(photoAlbumService.findPhotoAlbumByTitle("PhotoAlbum by " + topic.getName()), image1
                    , comment.getId().toString());
        }
        if (image2 != null) {
            photoService.save(photoAlbumService.findPhotoAlbumByTitle("PhotoAlbum by " + topic.getName()), image2
                    , comment.getId().toString());
        }

        CommentDto commentDto = commentService.assembleCommentDto(comment, user);
        return ResponseEntity.ok(commentDto);
    }

    //TODO Надо ли удалять Photo если оно есть в коментарии?
    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete comment from topic", description = "Delete comment by id", tags = {"Topic and comments"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment deleted"),
            @ApiResponse(responseCode = "404", description = "Error deleting comment")})
    @DeleteMapping(value = "/comment/delete/{commentId}", produces = {"application/json"})
    public ResponseEntity deleteComment(@PathVariable(value = "commentId") Long id) {
        Comment comment = commentService.getCommentById(id);
        boolean admin = securityUtilsService.isAdmin();
        boolean moderator = securityUtilsService.isModerator();
        User currentUser = securityUtilsService.getLoggedUser();
        User user = comment.getUser();
        if (comment.getId() == null || !currentUser.getId().equals(user.getId()) && !admin && !moderator) {
            return ResponseEntity.notFound().build();
        }
        comment.getTopic().setMessageCount(comment.getTopic().getMessageCount() - 1);
        topicService.editTopicByName(comment.getTopic());
        commentService.updatePostion(comment.getTopic().getId(), comment.getPosition());
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Update a comment", tags = {"Topic and comments"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = CommentDto.class))),
            @ApiResponse(responseCode = "400", description = "Error updating comment")})
    @PutMapping(value = "/comment/update", consumes = {"multipart/form-data"})
    public ResponseEntity<CommentDto> updateComment(@ModelAttribute @Valid CommentCreateAndUpdateDto messageComments,
                                                    @RequestParam(value = "commentID") Long commentID,
                                                    @RequestPart(required = false) MultipartFile image1,
                                                    @RequestPart(required = false) MultipartFile image2) {

        Comment comment = commentService.getCommentById(commentID);
        Topic topic = topicService.findById(messageComments.getIdTopic());
        User currentUser = securityUtilsService.getLoggedUser();
        User user = comment.getUser();
        List<ImageComment> images = new ArrayList<>();
        boolean admin = securityUtilsService.isAdmin();
        boolean moderator = securityUtilsService.isModerator();
        boolean allowedEditingTime = LocalDateTime.now().compareTo(comment.getDateTime().plusDays(7)) >= 0;
        boolean checkFirstImage = checkFileTypeService.isValidImageFile(image1);
        boolean checkSecondImage = checkFileTypeService.isValidImageFile(image2);

        comment.setTopic(topicService.findById(messageComments.getIdTopic()));
        comment.setCommentText(filterHtmlService.filterHtml(messageComments.getText()));
        if (messageComments.getAnswerID() != null) {
            comment.setAnswerTo(commentService.getCommentById(messageComments.getAnswerID()));
        } else {
            comment.setAnswerTo(null);
        }
        comment.setDateEdit(LocalDateTime.now());


        if (messageComments.getIdUser() == null || topic.isForbidComment() || currentUser == null || !currentUser.getId().equals(user.getId()) && !admin && !moderator || !admin && !moderator && !allowedEditingTime || !checkFirstImage || !checkSecondImage) {
            return ResponseEntity.badRequest().build();
        }
        commentService.updateComment(comment);

        if (image1 != null) {
            photoService.save(photoAlbumService.findPhotoAlbumByTitle("PhotoAlbum by " + topic.getName()), image1
                    , comment.getId().toString());
        }
        if (image2 != null) {
            photoService.save(photoAlbumService.findPhotoAlbumByTitle("PhotoAlbum by " + topic.getName()), image2
                    , comment.getId().toString());
        }
        CommentDto commentDto = commentService.assembleCommentDto(comment, user);
        List<Photo> photos = commentDto.getPhotos();
        if (!photos.isEmpty()){
            commentDto = deletePhoto(photos, image1, image2, commentDto);
        }
        return ResponseEntity.ok(commentDto);
    }

    public CommentDto deletePhoto(List<Photo> photos, MultipartFile image1, MultipartFile image2,  CommentDto commentDto){
        if (photos.size() == 1) {
            if (image1 == null || image2 == null) {
                long id = photos.get(0).getId();
               photos.clear();
                commentDto.setPhotos(photos);
                photoService.deletePhoto(id);
            }
        }
        if (photos.size() == 2) {
            long id = photos.get(0).getId();
            long id1 = photos.get(1).getId();
            if (image1 == null && image2 == null) {
                photoService.deletePhoto(id);
                photoService.deletePhoto(id1);
                photos.clear();
                commentDto.setPhotos(photos);
            }
            if (image1 == null && image2 != null) {
                photoService.deletePhoto(id1);
                Photo photoCopy = photos.get(1);
                photos.clear();
                photos.add(photoCopy);
                commentDto.setPhotos(photos);

            }
            if (image2 == null && image1 != null) {
                photoService.deletePhoto(id);
                Photo photoCopy = photos.get(0);
                photos.clear();
                photos.add(photoCopy);
                commentDto.setPhotos(photos);
            }
        }
        return commentDto;
    }

}
