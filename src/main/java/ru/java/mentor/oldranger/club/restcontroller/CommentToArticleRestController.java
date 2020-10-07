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
import ru.java.mentor.oldranger.club.dto.*;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.comment.ArticleComment;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.article.ArticleService;
import ru.java.mentor.oldranger.club.service.media.MediaService;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.FilterHtmlService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;
import ru.java.mentor.oldranger.club.service.utils.WritingBanService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/article")
@Tag(name = "Article comment")
public class CommentToArticleRestController {

    private final ArticleService articleService;
    private final UserService userService;
    private final SecurityUtilsService securityUtilsService;
    private final FilterHtmlService filterHtmlService;
    private WritingBanService writingBanService;
    private final PhotoAlbumService albumService;
    private final PhotoService photoService;
    private final PhotoAlbumService photoAlbumService;
    private final MediaService mediaService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get a article and a list of comments DTO", description = "Get a article and a list of comments for this article by article id", tags = {"Article comment"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArticleAndCommentsDto.class)))),
            @ApiResponse(responseCode = "204", description = "Article not found")})
    @GetMapping(value = "/comments", produces = {"application/json"})
    public ResponseEntity<ArticleAndCommentsDto> getArticleComments(@RequestParam("id") Long id) {
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Article article = articleService.getArticleById(id);
        if (article == null) {
            return ResponseEntity.noContent().build();
        }

        List<ArticleCommentDto> articleComments = articleService.getAllByArticle(article, currentUser);
        ArticleAndCommentsDto articleAndCommentsDto = new ArticleAndCommentsDto(article, articleComments);
        return ResponseEntity.ok(articleAndCommentsDto);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Add a comment to article", tags = {"Article comment"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ArticleCommentDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Error adding comment"),
            @ApiResponse(responseCode = "401",
                    description = "User have not authority")})
    @PostMapping(value = "/comment/add", consumes = {"multipart/form-data"})
    public ResponseEntity<ArticleCommentDto> addCommentToArticle(@ModelAttribute @Valid ReceivedCommentArticleDto receivedCommentDto,
                                                                 @RequestPart(required = false) MultipartFile image1,
                                                                 @RequestPart(required = false) MultipartFile image2) {
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String cleanedText = filterHtmlService.filterHtml(receivedCommentDto.getCommentText());

        if (image1 == null & image2 == null & articleService.isEmptyComment(cleanedText)) {
            return ResponseEntity.badRequest().build();
        }
        ArticleComment articleComment;

        Article article = articleService.getArticleById(receivedCommentDto.getIdArticle());
        User user = userService.findById(receivedCommentDto.getIdUser());
        LocalDateTime localDateTime = LocalDateTime.now();
        if (receivedCommentDto.getAnswerId() != null) {
            ArticleComment answer = articleService.getCommentById(receivedCommentDto.getAnswerId());
            articleComment = new ArticleComment(article, user, answer, localDateTime, cleanedText);
        } else {
            articleComment = new ArticleComment(article, user, null, localDateTime, cleanedText);
        }

        if (user == null || !currentUser.getId().equals(user.getId())) {
            return ResponseEntity.badRequest().build();
        }

        articleService.addCommentToArticle(articleComment);
        PhotoAlbum photoAlbum = new PhotoAlbum("PhotoAlbum by " + article.getTitle());
        photoAlbum.setMedia(mediaService.findMediaByUser(user));
        photoAlbum.setAllowView(false);
        albumService.save(photoAlbum);

        if (image1 != null) {
            photoService.save(photoAlbum, image1
                    , articleComment.getId().toString());
        }
        if (image2 != null) {
            photoService.save(photoAlbum, image2
                    , articleComment.getId().toString());
        }
        ArticleCommentDto commentDto = articleService.assembleCommentToDto(articleComment, user);

        return ResponseEntity.ok(commentDto);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Update a comment", tags = {"Article comment"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ArticleCommentDto.class))),
            @ApiResponse(responseCode = "400", description = "Error updating comment"),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @PutMapping(value = "/comment/update", consumes = {"multipart/form-data"})
    public ResponseEntity<ArticleCommentDto> updateArticleComment(@ModelAttribute @Valid ReceivedCommentArticleDto receivedCommentArticleDto,
                                                                  @RequestParam("commentID") Long commentID,
                                                                  @RequestParam String[] photoIdList,
                                                                  @RequestPart(required = false) MultipartFile image1,
                                                                  @RequestPart(required = false) MultipartFile image2) {

        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ArticleComment articleComment = articleService.getCommentById(commentID);
        User user = articleComment.getUser();

        boolean admin = securityUtilsService.isAdmin();
        boolean moderator = securityUtilsService.isModerator();
        boolean allowedEditingTime = LocalDateTime.now().compareTo(articleComment.getDateTime().plusDays(7)) >= 0;

        articleComment.setArticle(articleService.getArticleById(receivedCommentArticleDto.getIdArticle()));
        articleComment.setCommentText(receivedCommentArticleDto.getCommentText());
        articleComment.setDateTime(articleComment.getDateTime());
        if (receivedCommentArticleDto.getAnswerId() != null) {
            articleComment.setAnswerTo(articleService.getCommentById(receivedCommentArticleDto.getAnswerId()));
        } else {
            articleComment.setAnswerTo(null);
        }

        if (receivedCommentArticleDto.getIdUser() == null || !currentUser.getId().equals(user.getId()) && (!admin || !moderator) || (!admin || !moderator) && !allowedEditingTime) {
            return ResponseEntity.badRequest().build();
        }


        ArticleCommentDto articleCommentDto;

        articleCommentDto = articleService.assembleCommentToDto(articleComment, user);

        List<Photo> photos = articleCommentDto.getPhotos();

        String cleanedText = filterHtmlService.filterHtml(receivedCommentArticleDto.getCommentText());
        String idPhotosForDelete = photoIdList[0].replaceAll("[^0-9]", "");

        if (idPhotosForDelete.equals("") & articleService.isEmptyComment(cleanedText) & image1 == null & image2 == null) {
            return ResponseEntity.badRequest().build();
        }

        articleService.updateArticleComment(articleComment);

        List<Long> idPhotosToKeep = new ArrayList<>();
        for (int i = 0; i <= photoIdList.length - 1; i++) {
            String id = photoIdList[i].replaceAll("[^0-9]", "");
            if (!id.equals("")) {
                idPhotosToKeep.add(Long.parseLong(id));
            }
        }

        List<Long> idPhotosToDelete = new ArrayList<>();
        for (Photo photo : photos) {
            if (!idPhotosToKeep.contains(photo.getId()))
                idPhotosToDelete.add(photo.getId());
        }
        PhotoAlbum photoAlbum = photoAlbumService.findPhotoAlbumByTitle(articleComment.getId().toString());

        articleCommentDto = deletePhotoFromDto(photoAlbum, image1, image2, idPhotosToKeep, idPhotosToDelete, photos, articleCommentDto);
        articleCommentDto = updatePhotos(photoAlbum, image1, image2, articleComment, articleCommentDto, photos);

        return ResponseEntity.ok(articleCommentDto);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete comment from article", description = "Delete comment by id", tags = {"Article comment"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment deleted"),
            @ApiResponse(responseCode = "204", description = "Comment not found"),
            @ApiResponse(responseCode = "401", description = "User is not logged")})
    @DeleteMapping(value = "/comment/delete/{id}", produces = {"application/json"})
    public ResponseEntity<ArticleCommentDto> deleteArticleComment(@PathVariable(value = "id") Long id) {
        ArticleComment articleComment = articleService.getCommentById(id);
        User currentUser = securityUtilsService.getLoggedUser();
        User user = articleComment.getUser();

        if (!currentUser.getId().equals(user.getId()) && !securityUtilsService.isAdmin()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (articleComment.getId() == null) {
            return ResponseEntity.noContent().build();
        }

        ArticleCommentDto articleCommentDto;
        articleCommentDto = articleService.assembleCommentToDto(articleComment, user);
        List<Photo> photos = articleCommentDto.getPhotos();
        if (!photos.isEmpty()) {
            for (Photo photo : photos) {
                photoService.deletePhoto(photo.getId());
            }
        }

        List<ArticleComment> listChildComments = articleService.getChildComment(articleComment);
        if (!listChildComments.isEmpty()) {
            articleComment.setDeleted(true);
            articleComment.setCommentText("Комментарий был удален");
            articleService.updateArticleComment(articleComment);
        } else {
            articleService.deleteComment(id);
        }
        return ResponseEntity.ok().build();
    }

    private ArticleCommentDto updatePhotos(PhotoAlbum photoAlbum, MultipartFile image1, MultipartFile image2,
                                           ArticleComment comment, ArticleCommentDto articleCommentDto, List<Photo> photos) {
        if (image1 != null) {
            Photo newPhoto1 = photoService.save(photoAlbum, image1, comment.getId().toString());
            photos.add(newPhoto1);
        }
        if (image2 != null) {
            Photo newPhoto2 = photoService.save(photoAlbum, image2, comment.getId().toString());
            photos.add(newPhoto2);
        }
        articleCommentDto.setPhotos(photos);
        return articleCommentDto;
    }

    private ArticleCommentDto deletePhotoFromDto(PhotoAlbum photoAlbum, MultipartFile image1, MultipartFile image2,
                                                 List<Long> idPhotosToKeep, List<Long> idPhotosToDelete,
                                                 List<Photo> photos, ArticleCommentDto articleCommentDto) {

        Optional<Photo> thumbImageId = Optional.ofNullable(photoAlbum.getThumbImage());
        thumbImageId.ifPresent(v -> photoAlbum.getThumbImage().getId());

        if ((idPhotosToDelete.contains(thumbImageId)) & (image1 != null || image2 != null)) {
            if (idPhotosToDelete.size() == 1 & !idPhotosToKeep.contains(thumbImageId) & idPhotosToKeep.size() == 1) {
                photoService.deletePhotoByEditingComment(idPhotosToDelete.get(0));
                Photo photoToKeep = photoService.findById(idPhotosToKeep.get(0));
                photoAlbum.setThumbImage(photoToKeep);
                photoAlbumService.save(photoAlbum);
            }
            for (Long id : idPhotosToDelete) {
                photoService.deletePhotoByEditingComment(id);
            }
        } else if (!idPhotosToKeep.isEmpty()) {
            for (Photo photo : photos) {
                for (Long id : idPhotosToKeep) {
                    if (!id.equals(photo.getId())) {
                        photoService.deletePhoto(photo.getId());
                    }
                }
            }
        } else {
            for (Photo photo : photos) {
                photoService.deletePhoto(photo.getId());
            }
        }
        photos.clear();
        for (Long id : idPhotosToKeep) {
            photos.add(photoService.findById(id));
        }
        articleCommentDto.setPhotos(photos);
        return articleCommentDto;
    }
}

