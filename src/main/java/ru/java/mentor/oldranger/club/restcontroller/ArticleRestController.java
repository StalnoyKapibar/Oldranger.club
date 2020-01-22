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
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.article.ArticlesSection;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.RoleType;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.article.ArticleSectionService;
import ru.java.mentor.oldranger.club.service.article.ArticleService;
import ru.java.mentor.oldranger.club.service.article.ArticleTagService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/article")
@Tag(name = "Article")
public class ArticleRestController {

    private ArticleService articleService;
    private SecurityUtilsService securityUtilsService;
    private ArticleTagService articleTagService;
    private ArticleSectionService articleSectionService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get articles by tags", description = "Get articles by tags", tags = {"Article"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Article.class)))),
            @ApiResponse(responseCode = "204", description = "Articles not found")})
    @GetMapping(value = "/tag", produces = {"application/json"})
    public ResponseEntity<Page<Article>> getAllArticlesByTagId(@RequestParam Set<ArticleTag> tag_id,
                                                               @RequestParam(value = "page", required = false) Integer page) {

        if (page == null) {
            page = 0;
        }

        if (tag_id.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id"));
        Page<Article> articles = articleService.getAllByTag(tag_id, pageable);
        return ResponseEntity.ok(articles);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get articles by sections", description = "Get articles by sections", tags = {"Article"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Article.class)))),
            @ApiResponse(responseCode = "204", description = "Articles not found")})
    @GetMapping(value = "/section", produces = {"application/json"})
    public ResponseEntity<Page<Article>> getAllArticlesBySectionsId(@RequestParam("sections_id") Set<ArticlesSection> articlesSections,
                                                                  @RequestParam(value = "page", required = false) Integer page) {

        if (page == null) {
            page = 0;
        }

        if (articlesSections.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id"));
        Page<Article> articles = articleService.getAllBySections(articlesSections, pageable);
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
                                                 @RequestParam("tagsId") List<Long> tagsId,
                                                 @RequestParam("sectionId") List<Long> sectionId,
                                                 @RequestParam("isHideToAnon") boolean isHideToAnon) {
        User user = securityUtilsService.getLoggedUser();
        Set<ArticleTag> tagsArt = articleTagService.addTagsToSet(tagsId);
        Set<ArticlesSection> sections = articleSectionService.addSectionToSet(sectionId);
        if (tagsArt.size() == 0) {
            return ResponseEntity.noContent().build();
        }
        Article article = new Article(title, user, tagsArt, sections, LocalDateTime.now(), text, isHideToAnon);
        articleService.addArticle(article);
        return ResponseEntity.ok(article);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Update article", description = "Update article", tags = {"Article"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Article.class))),
            @ApiResponse(responseCode = "203", description = "You have no rights to edit this article"),
            @ApiResponse(responseCode = "204", description = "Article not found")})
    @PostMapping(value = "/update/{id}", produces = {"application/json"})
    public ResponseEntity<Article> updateArticleById(@PathVariable long id,
                                                     @RequestParam("title") String title,
                                                     @RequestParam("text") String text,
                                                     @RequestParam(value = "tagsId") List<Long> tagsId,
                                                     @RequestParam("isHideToAnon") boolean isHideToAnon) {
        Article article = articleService.getArticleById(id);
        if (article == null ) {
          return ResponseEntity.noContent().build();
        }
        int daysSinceLastEdit = (int) Duration.between(article.getDate(), LocalDateTime.now()).toDays();
        if (!securityUtilsService.isModerator() || !(article.getUser().equals(securityUtilsService.getLoggedUser()) && daysSinceLastEdit < 7)) {
            ResponseEntity.status(203).build();
        }
        article.setTitle(title);
        article.setText(text);
        Set<ArticleTag> tagsArt = articleTagService.addTagsToSet(tagsId);
        if (tagsArt.size() == 0) {
            return ResponseEntity.noContent().build();
        }
        article.setArticleTags(tagsArt);
        article.setHideToAnon(isHideToAnon);
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
        if (securityUtilsService.isModerator() || securityUtilsService.isAdmin()) {
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
        if (securityUtilsService.isModerator() || securityUtilsService.isAdmin()) {
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
