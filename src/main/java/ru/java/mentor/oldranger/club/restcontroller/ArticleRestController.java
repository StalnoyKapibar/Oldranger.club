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
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.RoleType;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.article.ArticleService;
import ru.java.mentor.oldranger.club.service.article.ArticleTagService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/api/article")
@Tag(name = "Article")
public class ArticleRestController {

    private ArticleService articleService;
    private SecurityUtilsService securityUtilsService;
    private ArticleTagService articleTagService;

    @GetMapping(value = "/tag/{tag_id}", produces = {"application/json"})
    public ResponseEntity<List<Article>> getAllNewsByTagId(@PathVariable long tag_id) {
        List<Article> articles = articleService.getAllByTag(tag_id);
        return ResponseEntity.ok(articles);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Add article", description = "Add new article", tags = {"Article"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Article.class))),})
    @PostMapping(value = "/add", produces = {"application/json"})
    public ResponseEntity<Article> addNewArticle(@RequestParam("title") String title,
                                                 @RequestParam("text") String text,
                                                 @RequestParam List<Long> tagsId) {
        User user = securityUtilsService.getLoggedUser();
        Set<ArticleTag> tagsArt = articleTagService.addTagsToSet(tagsId);
        if (tagsArt.size() == 0) {
            return ResponseEntity.noContent().build();
        }
        Article article = new Article(title, user, tagsArt, LocalDateTime.now(), text);
        articleService.addArticle(article);
        return ResponseEntity.ok(article);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Update article", description = "Update article", tags = {"Article"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Article.class))),})
    @PostMapping(value = "/update/{id}", produces = {"application/json"})
    public ResponseEntity<Article> updateArticleById(@PathVariable long id,
                                                     @RequestParam("title") String title,
                                                     @RequestParam("text") String text,
                                                     @RequestParam(value = "tagsId") List<Long> tagsId) {
        Article article = articleService.getArticleById(id);
        if (article == null || !securityUtilsService.isAuthorityReachableForLoggedUser(new Role("ROLE_ADMIN"))) {
            return ResponseEntity.noContent().build();
        }
        article.setTitle(title);
        article.setText(text);
        Set<ArticleTag> tagsArt = articleTagService.addTagsToSet(tagsId);
        if (tagsArt.size() == 0) {
            return ResponseEntity.noContent().build();
        }
        article.setArticleTags(tagsArt);
        articleService.addArticle(article);
        return ResponseEntity.ok(article);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete article", description = "Delete article", tags = {"Article"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete successful"),
            @ApiResponse(responseCode = "203", description = "Not have rule for delete article"),
            @ApiResponse(responseCode = "204", description = "Not found Article")})
    @DeleteMapping("/deleteArticle")
    public ResponseEntity deleteArticle(@RequestParam("idArticle") Long idArticle) {
        boolean isModer = securityUtilsService.isAuthorityReachableForLoggedUser(RoleType.ROLE_MODERATOR);
        boolean isAdmin = securityUtilsService.isAuthorityReachableForLoggedUser(RoleType.ROLE_ADMIN);
        if (isModer || isAdmin) {
            try {
                articleService.deleteArticle(idArticle);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.status(203).build();
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete articles", description = "Delete articles", tags = {"Article"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete successful"),
            @ApiResponse(responseCode = "203", description = "Not have rule for delete articles"),
            @ApiResponse(responseCode = "204", description = "Not found Articles")})
    @DeleteMapping("/deleteArticles")
    public ResponseEntity deleteArticles(@RequestParam("articlesIds") List<Long> ids) {
        boolean isModer = securityUtilsService.isAuthorityReachableForLoggedUser(RoleType.ROLE_MODERATOR);
        boolean isAdmin = securityUtilsService.isAuthorityReachableForLoggedUser(RoleType.ROLE_ADMIN);
        if (isModer || isAdmin) {
            try {
                articleService.deleteArticles(ids);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.status(203).build();
    }
}
