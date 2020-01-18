package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticlesSection;
import ru.java.mentor.oldranger.club.service.article.ArticleSectionService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/article")
@Tag(name = "Article Section")
public class ArticleSectionRestController {

    private ArticleSectionService articleSectionService;

    @GetMapping(value = "/sections/page", produces = {"application/json"})
    public ResponseEntity<Page<Article>> getArticlesPageBySection(@RequestParam("section") Long id,
                                                                  @RequestParam(value = "page", required = false) Integer page) {
        if (id == null) {
            return ResponseEntity.noContent().build();
        }
        ArticlesSection articlesSection = articleSectionService.getSectionById(id);

        if (page == null) {
            page = 0;
        }
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id"));
        Page<Article> articlePage = articleSectionService.getPageableArticlesBySection(articlesSection, pageable);
        return ResponseEntity.ok(articlePage);
    }

    @GetMapping(value = "/sections", produces = {"application/json"})
    public ResponseEntity<List<ArticlesSection>> getAllSections() {
        List<ArticlesSection> articlesSections = articleSectionService.getAllArticlesSection();
        return ResponseEntity.ok(articlesSections);
    }

    @GetMapping(value = "/section/{id}", produces = {"application/json"})
    public ResponseEntity<ArticlesSection> getSectionById(@PathVariable(value = "id") Long id) {

        if (id == null) {
            return ResponseEntity.noContent().build();
        }
        ArticlesSection articlesSection = articleSectionService.getSectionById(id);
        return ResponseEntity.ok(articlesSection);
    }

    @PostMapping(value = "/section/add", produces = {"application/json"})
    public ResponseEntity<ArticlesSection> createSection(@RequestParam("id") Long id,
                                                         @RequestParam("name") String name) {
        ArticlesSection articlesSection = new ArticlesSection(id, name);
        articleSectionService.addSection(articlesSection);
        return ResponseEntity.ok(articlesSection);
    }

    @PutMapping(value = "/section/edit", produces = {"application/json"})
    public ResponseEntity<ArticlesSection> editSection(@RequestParam("id") Long id,
                                                       @RequestParam("name") String name) {

        if (id == null) {
            return ResponseEntity.noContent().build();
        }
        ArticlesSection articlesSection = articleSectionService.getSectionById(id);
        articlesSection.setName(name);
        articleSectionService.editSection(articlesSection);
        return ResponseEntity.ok(articlesSection);
    }

    @DeleteMapping(value = "/section/delete/{id}")
    public ResponseEntity deleteSection(@PathVariable("id") Long id) {

        if (id == null) {
            return ResponseEntity.noContent().build();
        }
        articleSectionService.deleteSection(id);
        return ResponseEntity.ok().build();
    }

}
