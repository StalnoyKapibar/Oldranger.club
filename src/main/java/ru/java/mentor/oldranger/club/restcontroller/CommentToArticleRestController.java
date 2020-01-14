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
import ru.java.mentor.oldranger.club.dto.ReceivedCommentArticleDto;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleComment;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.article.ArticleService;
import ru.java.mentor.oldranger.club.service.user.RoleService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.time.LocalDateTime;

@RestController
@AllArgsConstructor
@RequestMapping("/api/article")
@Tag(name = "Article comment")
public class CommentToArticleRestController {

    private ArticleService articleService;
    private UserService userService;
    private SecurityUtilsService securityUtilsService;
    private RoleService roleService;


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
                                                                 @RequestParam("answerId") Long answerId,
                                                                 @RequestBody String commentText) {
        ReceivedCommentArticleDto receivedCommentDto = new ReceivedCommentArticleDto(idArticle, idUser, commentText, answerId);
        ArticleComment articleComment;

        User currentUser = securityUtilsService.getLoggedUser();
        Article article = articleService.getArticleById(receivedCommentDto.getIdArticle());
        User user = userService.findById(receivedCommentDto.getIdUser());
        LocalDateTime localDateTime = LocalDateTime.now();
        if (receivedCommentDto.getAnswerId() != 0) {
            ArticleComment answer = articleService.getCommentById(receivedCommentDto.getAnswerId());
            articleComment = new ArticleComment(article, user, answer, localDateTime, receivedCommentDto.getCommentText());
        } else {
            articleComment = new ArticleComment(article, user, null, localDateTime, receivedCommentDto.getCommentText());
        }

        if (user == null || !currentUser.getId().equals(user.getId())) {
            return ResponseEntity.badRequest().build();
        }
        articleService.addCommentToArticle(articleComment);
        ArticleCommentDto commentDto = articleService.conversionCommentToDto(articleComment);
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
                                                                  @RequestParam("answerId") Long answerId,
                                                                  @RequestBody String commentText) {

        ReceivedCommentArticleDto commentArticleDto = new ReceivedCommentArticleDto(idArticle, idUser, commentText, answerId);
        ArticleComment articleComment = articleService.getCommentById(commentID);
        User currentUser = securityUtilsService.getLoggedUser();
        User user = articleComment.getUser();

        boolean admin = securityUtilsService.isAuthorityReachableForLoggedUser(roleService.getRoleByAuthority("ROLE_ADMIN"));
        boolean moderator = securityUtilsService.isAuthorityReachableForLoggedUser(roleService.getRoleByAuthority("ROLE_MODERATOR"));
        boolean allowedEditingTime = LocalDateTime.now().compareTo(articleComment.getDateTime().plusDays(7)) >= 0;

        articleComment.setArticle(articleService.getArticleById(commentArticleDto.getIdArticle()));
        articleComment.setCommentText(commentArticleDto.getCommentText());
        articleComment.setDateTime(articleComment.getDateTime());
        if (commentArticleDto.getAnswerId() != 0) {
            articleComment.setAnswerTo(articleService.getCommentById(commentArticleDto.getAnswerId()));
        } else {
            articleComment.setAnswerTo(null);
        }

        if (commentArticleDto.getIdUser() == null || !currentUser.getId().equals(user.getId()) && (!admin || !moderator) || (!admin || !moderator) && !allowedEditingTime) {
            return ResponseEntity.badRequest().build();
        }

        articleService.updateArticleComment(articleComment);
        ArticleCommentDto articleCommentDto = articleService.conversionCommentToDto(articleComment);
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
        boolean admin = securityUtilsService.isAuthorityReachableForLoggedUser(roleService.getRoleByAuthority("ROLE_ADMIN"));
        User currentUser = securityUtilsService.getLoggedUser();
        User user = articleComment.getUser();

        if (articleComment.getId() == null) {
            return ResponseEntity.noContent().build();
        }

        if (!currentUser.getId().equals(user.getId()) && !admin) {
            return ResponseEntity.status(403).build();
        }
        articleService.deleteComment(id);
        return ResponseEntity.ok().build();
    }
}

