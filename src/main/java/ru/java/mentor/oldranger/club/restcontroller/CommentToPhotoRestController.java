package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.ArticleCommentDto;
import ru.java.mentor.oldranger.club.dto.PhotoCommentDto;
import ru.java.mentor.oldranger.club.dto.ReceivedCommentArticleDto;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.comment.ArticleComment;
import ru.java.mentor.oldranger.club.model.comment.PhotoComment;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.time.LocalDateTime;


@RestController
@AllArgsConstructor
@RequestMapping("/api/photo/comment")
@Tag(name = "Comment to photo")
public class CommentToPhotoRestController {

    private SecurityUtilsService securityUtilsService;
    private PhotoService photoService;
    private UserService userService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Add comment to photo", description = "Add comment to photo", tags = {"Comment to photo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = PhotoCommentDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Error adding comment")})
    @PostMapping(value = "/add", produces = {"application/json"})
    public ResponseEntity<PhotoCommentDto> addNewComment(@RequestParam("idPhoto") Long idPhoto,
                                                         @RequestParam("idUser") Long idUser,
                                                         @RequestParam(value="answerId", required = false) Long answerId,
                                                         @RequestBody String commentText) {
        User currentUser = securityUtilsService.getLoggedUser();
        Photo photo = photoService.findById(idPhoto);
        User user = userService.findById(idUser);
        PhotoComment photoComment;
        LocalDateTime localDateTime = LocalDateTime.now();
        if(answerId != null) {
            PhotoComment answer = photoService.getCommentById(answerId);
            photoComment = new PhotoComment(photo, user, answer, localDateTime, commentText);
        } else {
            photoComment = new PhotoComment(photo, user, null, localDateTime, commentText);
        }
        if (user == null || !currentUser.getId().equals(user.getId())) {
            return ResponseEntity.badRequest().build();
        }
        photoService.addCommentToPhoto(photoComment);
        return ResponseEntity.ok(photoService.conversionCommentToDto(photoComment));
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete comment from photo", description = "Delete comment by id", tags = {"Comment to photo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment deleted"),
            @ApiResponse(responseCode = "204", description = "Comment not found"),
            @ApiResponse(responseCode = "403", description = "User is not logged")})
    @DeleteMapping(value = "/delete/{id}", produces = {"application/json"})
    public ResponseEntity deletePhotoComment(@PathVariable(value = "id") Long id) {
        PhotoComment photoComment = photoService.getCommentById(id);
        User currentUser = securityUtilsService.getLoggedUser();
        User user = photoComment.getUser();

        if (photoComment.getId() == null) {
            return ResponseEntity.noContent().build();
        }

        if (!currentUser.getId().equals(user.getId()) && !securityUtilsService.isAdmin()) {
            return ResponseEntity.status(403).build();
        }
        photoService.deleteComment(id);
        return ResponseEntity.ok().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Update a comment", tags = {"Comment to photo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = PhotoCommentDto.class))),
            @ApiResponse(responseCode = "400", description = "Error updating comment")})
    @PutMapping(value = "/update", produces = {"application/json"})
    public ResponseEntity<PhotoCommentDto> updatePhotoComment(@RequestParam("commentID") Long commentID,
                                                                  @RequestParam("idPhoto") Long idPhoto,
                                                                  @RequestParam("idUser") Long idUser,
                                                                  @RequestParam(value="answerId", required = false) Long answerId,
                                                                  @RequestBody String commentText) {
        PhotoComment photoComment = photoService.getCommentById(commentID);
        User currentUser = securityUtilsService.getLoggedUser();
        User user = photoComment.getUser();

        boolean admin = securityUtilsService.isAdmin();
        boolean moderator = securityUtilsService.isModerator();
        boolean allowedEditingTime = LocalDateTime.now().compareTo(photoComment.getDateTime().plusDays(7)) >= 0;

        photoComment.setPhoto(photoService.findById(idPhoto));
        photoComment.setCommentText(commentText);
        photoComment.setDateTime(photoComment.getDateTime());
        if (answerId != null) {
            photoComment.setAnswerTo(photoService.getCommentById(answerId));
        } else {
            photoComment.setAnswerTo(null);
        }

        if (idPhoto == null || !currentUser.getId().equals(user.getId()) && (!admin || !moderator) || (!admin || !moderator) && !allowedEditingTime) {
            return ResponseEntity.badRequest().build();
        }

        photoService.updatePhotoComment(photoComment);
        PhotoCommentDto photoCommentDto = photoService.conversionCommentToDto(photoComment);
        return ResponseEntity.ok(photoCommentDto);
    }
}
