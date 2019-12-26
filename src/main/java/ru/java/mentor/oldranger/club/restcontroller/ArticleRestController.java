package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.article.ArticleService;
import ru.java.mentor.oldranger.club.service.article.ArticleTagService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.time.LocalDateTime;
import java.util.HashSet;
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

    @GetMapping(value = "/tag/{tag_id}", produces = { "application/json" })
    public ResponseEntity<List<Article>> getAllNewsByTagId(@PathVariable long tag_id) {
        List<Article> articles = articleService.getAllByTag(tag_id);
        return ResponseEntity.ok(articles);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Add article",description = "Add new article", tags = {"Article"})
    @PostMapping(value = "/add", produces = {"application/json"})
    public ResponseEntity<Article> addNewArticle(@RequestParam("title") String title,
                                                 @RequestParam("text") String text,
                                                 @RequestParam(value="tags") List<String> tags) {
        User user = securityUtilsService.getLoggedUser();
        Set<ArticleTag> tagsArt = new HashSet<>();
        for(String tag : tags){
            ArticleTag articleTag = new ArticleTag();
            articleTag.setName(tag);
            articleTagService.addTag(articleTag);
            tagsArt.add(articleTag);
        }
        Article article = new Article(title, user, tagsArt, LocalDateTime.now(), text);
        articleService.addArticle(article);
        return ResponseEntity.ok(article);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Update article", description = "Update article", tags = {"Article"})
    @PostMapping(value = "/update/{id}", produces = {"application/json"})
    public ResponseEntity<Article> updateArticleById(@PathVariable long id,
                                                     @RequestParam("title") String title,
                                                     @RequestParam("text") String text,
                                                     @RequestParam(value="tags") List<String> tags) {
        Article article = articleService.getArticleById(id);
        article.setTitle(title);
        article.setText(text);
        Set<ArticleTag> tagsArt = new HashSet<>();
        for(int i = 0; i < tags.size(); i++){
            ArticleTag articleTag = new ArticleTag();
            articleTag.setName(tags.get(i));
            articleTagService.addTag(articleTag);
            tagsArt.add(articleTag);
        }
        article.setArticleTags(tagsArt);
        articleService.addArticle(article);
        return ResponseEntity.ok(article);
    }
}
