package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.service.article.ArticleService;
import ru.java.mentor.oldranger.club.service.article.ArticleTagService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/article")
@Tag(name = "Article")
public class ArticleRestController {

    private ArticleTagService articleTagService;
    private ArticleService articleService;

    @GetMapping(value = "/tag/{tag_id}", produces = { "application/json" })
    public ResponseEntity<List<Article>> getAllNewsByTagId(@PathVariable long tag_id,
                                                           @RequestParam(value = "recursive", required = false) boolean recursive) {

        List<Article> articles = recursive ? articleService.getAllByTagRecursive(articleTagService.getTagById(tag_id)) :
                                             articleService.getAllByTag(articleTagService.getTagById(tag_id));

        return ResponseEntity.ok(articles);
    }
}
