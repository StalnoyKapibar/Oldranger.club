package ru.java.mentor.oldranger.club.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.java.mentor.oldranger.club.model.news.NewsTag;
import ru.java.mentor.oldranger.club.service.news.NewsService;
import ru.java.mentor.oldranger.club.service.news.NewsTagService;

@Controller
@RequestMapping(path = "/test/news")
public class TestNewsController {

    private NewsTagService newsTagService;
    private NewsService newsService;

    public TestNewsController(NewsTagService newsTagService, NewsService newsService) {
        this.newsTagService = newsTagService;
        this.newsService = newsService;
    }

    @GetMapping
    public String getNews(Model model) {
        model.addAttribute("tagList", newsTagService.getAllTags());
        model.addAttribute("newsList", newsService.getAllNews());
        return "testNews";
    }

    @GetMapping(path = "/t/{tagId}")
    public String getNewsByTag(@PathVariable(name = "tagId") Long tagId, Model model) {
        model.addAttribute("tagList", newsTagService.getAllTags());
        NewsTag newsTag = newsTagService.getTagById(tagId);
        model.addAttribute("newsList", newsService.getAllByTag(newsTag));
        return "testNews";
    }
}
