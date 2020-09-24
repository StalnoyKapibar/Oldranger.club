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
import ru.java.mentor.oldranger.club.dto.ArticleAndCommentsDto;
import ru.java.mentor.oldranger.club.dto.ArticleCommentDto;
import ru.java.mentor.oldranger.club.dto.ReceivedCommentArticleDto;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.comment.ArticleComment;
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

import java.time.LocalDateTime;
import java.util.List;

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
    private MediaService mediaService;
    private PhotoAlbumService albumService;
    private final PhotoAlbumService photoAlbumService;
    private final PhotoService photoService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get a article and a list of comments DTO", description = "Get a article and a list of comments for this article by article id", tags = {"Article comment"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArticleAndCommentsDto.class)))),
            @ApiResponse(responseCode = "204", description = "Article not found")})
    @GetMapping(value = "/comments", produces = {"application/json"})
    public ResponseEntity<ArticleAndCommentsDto> getArticleComments(@RequestParam("id") Long id) {

        Article article = articleService.getArticleById(id);
        if (article == null) {
            return ResponseEntity.noContent().build();
        }

        List<ArticleCommentDto> articleComments = articleService.getAllByArticle(article);
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
    @PostMapping(value = "/comment/add", produces = {"application/json"})
    public ResponseEntity<ArticleCommentDto> addCommentToArticle(@RequestParam("idArticle") Long idArticle,
                                                                 @RequestParam("idUser") Long idUser,
                                                                 @RequestParam(value = "answerId", required = false) Long answerId,
                                                                 @RequestBody String commentText,
                                                                 @RequestPart(required = false) MultipartFile image1,
                                                                 @RequestPart(required = false) MultipartFile image2) {
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ReceivedCommentArticleDto receivedCommentDto = new ReceivedCommentArticleDto(idArticle, idUser, filterHtmlService.filterHtml(commentText), answerId);
        ArticleComment articleComment;

        Article article = articleService.getArticleById(receivedCommentDto.getIdArticle());
        User user = userService.findById(receivedCommentDto.getIdUser());
        LocalDateTime localDateTime = LocalDateTime.now();
        if (receivedCommentDto.getAnswerId() != null) {
            ArticleComment answer = articleService.getCommentById(receivedCommentDto.getAnswerId());
            articleComment = new ArticleComment(article, user, answer, localDateTime, receivedCommentDto.getCommentText());
        } else {
            articleComment = new ArticleComment(article, user, null, localDateTime, receivedCommentDto.getCommentText());
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
                    , article.getId().toString());
        }
        if (image2 != null) {
            photoService.save(photoAlbum, image2
                    , article.getId().toString());
        }
        ArticleCommentDto commentDto = articleService.assembleCommentToDto(articleComment);

        return ResponseEntity.ok(commentDto);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Update a comment", tags = {"Article comment"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ArticleCommentDto.class))),
            @ApiResponse(responseCode = "400", description = "Error updating comment"),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @PutMapping(value = "/comment/update", produces = {"application/json"})
    public ResponseEntity<ArticleCommentDto> updateArticleComment(@RequestParam("commentID") Long commentID,
                                                                  @RequestParam("idArticle") Long idArticle,
                                                                  @RequestParam("idUser") Long idUser,
                                                                  @RequestParam(value = "answerId", required = false) Long answerId,
                                                                  @RequestBody String commentText) {

        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ReceivedCommentArticleDto commentArticleDto = new ReceivedCommentArticleDto(idArticle, idUser, filterHtmlService.filterHtml(commentText), answerId);
        ArticleComment articleComment = articleService.getCommentById(commentID);
        User user = articleComment.getUser();

        boolean admin = securityUtilsService.isAdmin();
        boolean moderator = securityUtilsService.isModerator();
        boolean allowedEditingTime = LocalDateTime.now().compareTo(articleComment.getDateTime().plusDays(7)) >= 0;

        articleComment.setArticle(articleService.getArticleById(commentArticleDto.getIdArticle()));
        articleComment.setCommentText(commentArticleDto.getCommentText());
        articleComment.setDateTime(articleComment.getDateTime());
        if (commentArticleDto.getAnswerId() != null) {
            articleComment.setAnswerTo(articleService.getCommentById(commentArticleDto.getAnswerId()));
        } else {
            articleComment.setAnswerTo(null);
        }

        if (commentArticleDto.getIdUser() == null || !currentUser.getId().equals(user.getId()) && (!admin || !moderator) || (!admin || !moderator) && !allowedEditingTime) {
            return ResponseEntity.badRequest().build();
        }

        articleService.updateArticleComment(articleComment);
        ArticleCommentDto articleCommentDto = articleService.assembleCommentToDto(articleComment);
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
}

