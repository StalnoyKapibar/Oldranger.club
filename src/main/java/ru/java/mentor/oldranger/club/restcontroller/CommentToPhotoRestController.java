package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
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
import ru.java.mentor.oldranger.club.dto.PhotoCommentDto;
import ru.java.mentor.oldranger.club.model.comment.PhotoComment;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BanType;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.utils.FilterHtmlService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;
import ru.java.mentor.oldranger.club.service.utils.WritingBanService;

import java.time.LocalDateTime;


@RestController
@AllArgsConstructor
@RequestMapping("/api/photo/comment")
@Tag(name = "Comment to photo")
public class CommentToPhotoRestController {

    private final SecurityUtilsService securityUtilsService;
    private final PhotoService photoService;
    private final FilterHtmlService filterHtmlService;
    private WritingBanService writingBanService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Add comment to photo", description = "Add comment to photo", tags = {"Comment to photo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = PhotoCommentDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Error adding comment"),
            @ApiResponse(responseCode = "401",
                    description = "User have not authority")})
    @PostMapping(value = "/add", produces = {"application/json"})
    public ResponseEntity<PhotoCommentDto> addNewComment(@RequestParam("idPhoto") Long idPhoto,
                                                         @RequestParam("commentText") String commentText) {
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (writingBanService.isForbidden(currentUser, BanType.ON_COMMENTS)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Photo photo = photoService.findById(idPhoto);
        if (photo == null) {
            return ResponseEntity.badRequest().build();
        }
        LocalDateTime localDateTime = LocalDateTime.now();
        PhotoComment photoComment = new PhotoComment(photo, currentUser, localDateTime, filterHtmlService.filterHtml(commentText));
        PhotoAlbum photoAlbum = photo.getAlbum();
        if (!photoAlbum.getViewers().contains(currentUser) && !securityUtilsService.isAdmin() &&
                !securityUtilsService.isModerator() && photoAlbum.getViewers().size() != 0) {
            return ResponseEntity.badRequest().build();
        }
        photoService.addCommentToPhoto(photoComment);
        return ResponseEntity.ok(photoService.assembleCommentDto(photoComment));
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete comment from photo", description = "Delete comment by id", tags = {"Comment to photo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment deleted"),
            @ApiResponse(responseCode = "400", description = "Error delete comment"),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @DeleteMapping(value = "/delete/{id}", produces = {"application/json"})
    public ResponseEntity deletePhotoComment(@PathVariable(value = "id") Long id) {
        PhotoComment photoComment = photoService.getCommentById(id);
        if (photoComment == null) {
            return ResponseEntity.badRequest().build();
        }
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (writingBanService.isForbidden(currentUser, BanType.ON_COMMENTS)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        User user = photoComment.getUser();
        if (!currentUser.getId().equals(user.getId()) && !securityUtilsService.isAdmin() && !securityUtilsService.isModerator()) {
            return ResponseEntity.badRequest().build();
        }
        photoService.deleteComment(id);
        return ResponseEntity.ok().build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Update a comment", tags = {"Comment to photo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = PhotoCommentDto.class))),
            @ApiResponse(responseCode = "400", description = "Error updating comment"),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @PutMapping(value = "/update", produces = {"application/json"})
    public ResponseEntity<PhotoCommentDto> updatePhotoComment(@RequestParam("commentID") Long commentID,
                                                              @RequestParam("commentText") String commentText) {
        PhotoComment photoComment = photoService.getCommentById(commentID);
        if (photoComment == null) {
            return ResponseEntity.badRequest().build();
        }
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (writingBanService.isForbidden(currentUser, BanType.ON_COMMENTS)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        User user = photoComment.getUser();
        boolean allowedEditingTime = LocalDateTime.now().compareTo(photoComment.getDateTime().plusDays(7)) >= 0;
        if (!currentUser.getId().equals(user.getId()) && !securityUtilsService.isAdmin() && !securityUtilsService.isModerator()) {
            return ResponseEntity.badRequest().build();
        }
        if (!securityUtilsService.isAdmin() && !securityUtilsService.isModerator() && allowedEditingTime) {
            return ResponseEntity.badRequest().build();
        }
        photoComment.setCommentText(filterHtmlService.filterHtml(commentText));
        photoComment.setDateTime(photoComment.getDateTime());
        photoService.updatePhotoComment(photoComment);
        PhotoCommentDto photoCommentDto = photoService.assembleCommentDto(photoComment);
        return ResponseEntity.ok(photoCommentDto);
    }
}
