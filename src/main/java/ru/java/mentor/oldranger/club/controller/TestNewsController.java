package ru.java.mentor.oldranger.club.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.service.article.ArticleService;
import ru.java.mentor.oldranger.club.service.article.ArticleTagService;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(path = "/test/news")
public class TestNewsController {

    private ArticleTagService articleTagService;
    private ArticleService articleService;

    public TestNewsController(ArticleTagService articleTagService, ArticleService articleService) {
        this.articleTagService = articleTagService;
        this.articleService = articleService;
    }

    @GetMapping
    public String getNews(Model model) {
        model.addAttribute("tagList", articleTagService.getAllTags());
        model.addAttribute("newsList", articleService.getAllArticles());
        return "testNews";
    }

    @GetMapping(path = "/t/{tagId}")
    public String getNewsByTag(@PathVariable(name = "tagId") Long tagId, Model model) {
        model.addAttribute("tagList", articleTagService.getAllTags());
        model.addAttribute("newsList", articleService.getAllByTag(tagId, Pageable.unpaged()));
        return "testNews";
    }
}
