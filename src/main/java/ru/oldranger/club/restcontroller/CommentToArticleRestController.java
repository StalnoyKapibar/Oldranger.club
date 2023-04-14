package ru.oldranger.club.restcontroller;


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
import ru.oldranger.club.dto.ArticleAndCommentsDto;
import ru.oldranger.club.dto.ArticleCommentDto;
import ru.oldranger.club.dto.ReceivedCommentArticleDto;
import ru.oldranger.club.model.article.Article;
import ru.oldranger.club.model.comment.ArticleComment;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.service.article.ArticleService;
import ru.oldranger.club.service.user.UserService;
import ru.oldranger.club.service.utils.FilterHtmlService;
import ru.oldranger.club.service.utils.SecurityUtilsService;

import java.time.LocalDateTime;

@RestController
@AllArgsConstructor
@RequestMapping("/api/article")
@Tag(name = "Article comment")
public class CommentToArticleRestController {

    private final ArticleService articleService;
    private final UserService userService;
    private final SecurityUtilsService securityUtilsService;
    private final FilterHtmlService filterHtmlService;


    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get a article and a list of comments DTO", description = "Get a article and a list of comments for this article by article id", tags = {"Article comment"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ArticleAndCommentsDto.class)))),
            @ApiResponse(responseCode = "204", description = "Article not found")})
    @GetMapping(value = "/comments", produces = {"application/json"})
    public ResponseEntity<ArticleAndCommentsDto> getArticleComments(@RequestParam("id") Long id,
                                                                    @RequestParam(value = "page", required = false) Integer page) {

        Article article = articleService.getArticleById(id);
        if (article == null) {
            return ResponseEntity.noContent().build();
        }

        if (page == null) {
            page = 0;
        }

        Pageable pageable = PageRequest.of(page, 10, Sort.by("id"));
        Page<ArticleCommentDto> articleComments = articleService.getAllByArticle(article, pageable);
        ArticleAndCommentsDto articleAndCommentsDto = new ArticleAndCommentsDto(article, articleComments);
        return ResponseEntity.ok(articleAndCommentsDto);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Add a comment to article", tags = {"Article comment"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ArticleCommentDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Error adding comment")})
    @PostMapping(value = "/comment/add", produces = {"application/json"})
    public ResponseEntity<ArticleCommentDto> addCommentToArticle(@RequestParam("idArticle") Long idArticle,
                                                                 @RequestParam("idUser") Long idUser,
                                                                 @RequestParam(value = "answerId", required = false) Long answerId,
                                                                 @RequestBody String commentText) {
        ReceivedCommentArticleDto receivedCommentDto = new ReceivedCommentArticleDto(idArticle, idUser, filterHtmlService.filterHtml(commentText), answerId);
        ArticleComment articleComment;

        User currentUser = securityUtilsService.getLoggedUser();
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
        ArticleCommentDto commentDto = articleService.assembleCommentToDto(articleComment);
        return ResponseEntity.ok(commentDto);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Update a comment", tags = {"Article comment"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ArticleCommentDto.class))),
            @ApiResponse(responseCode = "400", description = "Error updating comment")})
    @PutMapping(value = "/comment/update", produces = {"application/json"})
    public ResponseEntity<ArticleCommentDto> updateArticleComment(@RequestParam("commentID") Long commentID,
                                                                  @RequestParam("idArticle") Long idArticle,
                                                                  @RequestParam("idUser") Long idUser,
                                                                  @RequestParam(value = "answerId", required = false) Long answerId,
                                                                  @RequestBody String commentText) {

        ReceivedCommentArticleDto commentArticleDto = new ReceivedCommentArticleDto(idArticle, idUser, filterHtmlService.filterHtml(commentText), answerId);
        ArticleComment articleComment = articleService.getCommentById(commentID);
        User currentUser = securityUtilsService.getLoggedUser();
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
            @ApiResponse(responseCode = "403", description = "User is not logged")})
    @DeleteMapping(value = "/comment/delete/{id}", produces = {"application/json"})
    public ResponseEntity<ArticleCommentDto> deleteArticleComment(@PathVariable(value = "id") Long id) {
        ArticleComment articleComment = articleService.getCommentById(id);
        User currentUser = securityUtilsService.getLoggedUser();
        User user = articleComment.getUser();

        if (articleComment.getId() == null) {
            return ResponseEntity.noContent().build();
        }

        if (!currentUser.getId().equals(user.getId()) && !securityUtilsService.isAdmin()) {
            return ResponseEntity.status(403).build();
        }
        articleService.deleteComment(id);
        return ResponseEntity.ok().build();
    }
}

