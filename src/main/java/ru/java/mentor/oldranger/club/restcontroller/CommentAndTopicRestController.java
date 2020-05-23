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
import ru.java.mentor.oldranger.club.dto.CommentCreateAndUpdateDto;
import ru.java.mentor.oldranger.club.dto.CommentDto;
import ru.java.mentor.oldranger.club.dto.TopicAndCommentsDTO;
import ru.java.mentor.oldranger.club.model.comment.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.forum.TopicVisitAndSubscription;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BanType;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.forum.TopicVisitAndSubscriptionService;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.CheckFileTypeService;
import ru.java.mentor.oldranger.club.service.utils.FilterHtmlService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;
import ru.java.mentor.oldranger.club.service.utils.WritingBanService;

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
    private WritingBanService writingBanService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get a topic and a list of comments DTO", description = "Get a topic and a list of comments for this topic by topic id", tags = {"Topic and comments"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TopicAndCommentsDTO.class)))),
            @ApiResponse(responseCode = "204", description = "invalid topic id"),
            @ApiResponse(responseCode = "400", description = "topic is hide to anon"),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (limit == null) {
            limit = 10;
        }

        if (page == null) page = 0;
        Pageable pageable = PageRequest.of(page, limit, Sort.by("dateTime"));

        if (position == null) {
            position = 0;
        }
        List<TopicVisitAndSubscription> topicVisitAndSubscriptions = topicVisitAndSubscriptionService.getTopicVisitAndSubscriptionForTopic(topic);
        boolean isSubscribed = topicVisitAndSubscriptions.stream().filter(t -> t.getTopic().getId().equals(topic.getId())).anyMatch(TopicVisitAndSubscription::isSubscribed);

        Page<CommentDto> dtos = commentService.getPageableCommentDtoByTopic(topic, pageable, position, currentUser);
        TopicAndCommentsDTO topicAndCommentsDTO = new TopicAndCommentsDTO(topic, isSubscribed, dtos);
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
                    description = "Error adding comment"),
            @ApiResponse(responseCode = "401",
                    description = "User have not authority")})
    @PostMapping(value = "/comment/add", consumes = {"multipart/form-data"})
    public ResponseEntity<CommentDto> addMessageOnTopic(@ModelAttribute @Valid CommentCreateAndUpdateDto messageCommentEntity,
                                                        @RequestPart(required = false) MultipartFile image1,
                                                        @RequestPart(required = false) MultipartFile image2) {
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        /*if (currentUser.getMute().size() != 0 && currentUser.getMute().contains("ON_COMMENTS")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }*/

        String cleanedText = filterHtmlService.filterHtml(messageCommentEntity.getText());
        if (image1 == null & image2 == null & commentService.isEmptyComment(cleanedText)) {
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

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete comment from topic", description = "Delete comment by id", tags = {"Topic and comments"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment deleted"),
            @ApiResponse(responseCode = "404", description = "Error deleting comment"),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @DeleteMapping(value = "/comment/delete/{commentId}", produces = {"application/json"})
    public ResponseEntity deleteComment(@PathVariable(value = "commentId") Long id) {
        Comment comment = commentService.getCommentById(id);
        boolean admin = securityUtilsService.isAdmin();
        boolean moderator = securityUtilsService.isModerator();
        User currentUser = securityUtilsService.getLoggedUser();
        User user = comment.getUser();

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }


        /*if (writingBanService.isForbidden(currentUser, BanType.ON_COMMENTS)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }*/

        if (comment.getId() == null || !currentUser.getId().equals(user.getId()) && !admin && !moderator) {
            return ResponseEntity.notFound().build();
        }

        CommentDto commentDto = commentService.assembleCommentDto(comment, currentUser);
        List<Photo> photos = commentDto.getPhotos();
        if (!photos.isEmpty()) {
            for (Photo photo : photos) {
                photoService.deletePhoto(photo.getId());
            }
        }

        List<Comment> listChildComments = commentService.getChildComment(comment);
        if (!listChildComments.isEmpty()) {
            comment.setDeleted(true);
            comment.setCommentText("<<Комментарий был удален>>");
            commentService.updateComment(comment);
        } else {
            comment.getTopic().setMessageCount(comment.getTopic().getMessageCount() - 1);
            topicService.editTopicByName(comment.getTopic());
            commentService.updatePostion(comment.getTopic().getId(), comment.getPosition());
            commentService.deleteComment(id);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Update a comment", tags = {"Topic and comments"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = CommentDto.class))),
            @ApiResponse(responseCode = "400", description = "Error updating comment"),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @PutMapping(value = "/comment/update", consumes = {"multipart/form-data"})
    public ResponseEntity<CommentDto> updateComment(@ModelAttribute @Valid CommentCreateAndUpdateDto messageComments,
                                                    @RequestParam(value = "commentID") Long commentID,
                                                    @RequestParam String[] photoIdList,
                                                    @RequestPart(required = false) MultipartFile image1,
                                                    @RequestPart(required = false) MultipartFile image2) {

        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Comment comment = commentService.getCommentById(commentID);
        Comment answerComment = comment.getAnswerTo();
        if (answerComment != null) {
            messageComments.setAnswerID(comment.getAnswerTo().getId());
        }
        Topic topic = topicService.findById(messageComments.getIdTopic());
        User user = comment.getUser();

        if (ifUserAllowedToEditComment(comment, image1, image2, messageComments, topic, currentUser, user)) {
            return ResponseEntity.badRequest().build();
        }

        comment = setInfoIntoComment(comment, messageComments);
        CommentDto commentDto = commentService.assembleCommentDto(comment, user);
        List<Photo> photos = commentDto.getPhotos();

        String idPhotosForDelete = photoIdList[0].replaceAll("[^0-9]", "");

        String cleanedText = filterHtmlService.filterHtml(messageComments.getText());
        if (idPhotosForDelete.equals("") & commentService.isEmptyComment(cleanedText) & image1 == null & image2 == null) {
            return ResponseEntity.badRequest().build();
        }

        commentService.updateComment(comment);

        List<Long> idDeletePhotos = new ArrayList<>();
        for (int i = 0; i <= photoIdList.length - 1; i++) {
            String id = photoIdList[i].replaceAll("[^0-9]", "");
            if (!id.equals("")) {
                idDeletePhotos.add(Long.parseLong(id));
            }
        }

        commentDto = deletePhotoFromDto(idDeletePhotos, photos, commentDto);
        CommentDto updatedCommentDto = updatePhotos(image1, image2, comment, topic, commentDto, photos);

        return ResponseEntity.ok(updatedCommentDto);
    }

    private boolean ifUserAllowedToEditComment(Comment comment, MultipartFile image1, MultipartFile image2,
                                               CommentCreateAndUpdateDto messageComments,
                                               Topic topic, User currentUser, User user) {
        boolean admin = securityUtilsService.isAdmin();
        boolean moderator = securityUtilsService.isModerator();
        boolean allowedEditingTime = LocalDateTime.now().compareTo(comment.getDateTime().plusDays(7)) < 0;
        boolean checkFirstImage = checkFileTypeService.isValidImageFile(image1);
        boolean checkSecondImage = checkFileTypeService.isValidImageFile(image2);
        return messageComments.getIdUser() == null || topic.isForbidComment() || currentUser == null
                || !currentUser.getId().equals(user.getId()) && !admin && !moderator
                || !admin && !moderator && !allowedEditingTime || !checkFirstImage || !checkSecondImage;
    }

    private Comment setInfoIntoComment(Comment comment, CommentCreateAndUpdateDto messageComments) {
        comment.setTopic(topicService.findById(messageComments.getIdTopic()));
        comment.setCommentText(filterHtmlService.filterHtml(messageComments.getText()));
        if (messageComments.getAnswerID() != null) {
            comment.setAnswerTo(commentService.getCommentById(messageComments.getAnswerID()));
        } else {
            comment.setAnswerTo(null);
        }
        comment.setDateTime(comment.getDateTime());
        return comment;
    }

    private CommentDto updatePhotos(MultipartFile image1, MultipartFile image2,
                                    Comment comment, Topic topic,
                                    CommentDto commentDto, List<Photo> photos) {
        if (image1 != null) {
            PhotoAlbum photoAlbum = photoAlbumService.findPhotoAlbumByTitle("PhotoAlbum by " + topic.getName());
            Photo newPhoto1 = photoService.save(photoAlbum, image1, comment.getId().toString());
            photos.add(newPhoto1);
            commentDto.setPhotos(photos);
        }
        if (image2 != null) {
            Photo newPhoto2 = photoService.save(photoAlbumService.findPhotoAlbumByTitle("PhotoAlbum by "
                    + topic.getName()), image2, comment.getId().toString());
            photos.add(newPhoto2);
            commentDto.setPhotos(photos);
        }
        return commentDto;
    }

    private CommentDto deletePhotoFromDto(List<Long> idDeletePhotos, List<Photo> photos, CommentDto commentDto) {
        if (!idDeletePhotos.isEmpty()) {
            for (Photo photo : photos) {
                for (Long id : idDeletePhotos) {
                    if (!id.equals(photo.getId())) {
                        photoService.deletePhoto(photo.getId());
                    }
                }
            }
            photos.clear();
            for (Long id : idDeletePhotos) {
                photos.add(photoService.findById(id));
            }
            commentDto.setPhotos(photos);
        } else {
            for (Photo photo : photos) {
                photoService.deletePhoto(photo.getId());
            }
            photos.clear();
            commentDto.setPhotos(photos);
        }
        return commentDto;
    }
}


