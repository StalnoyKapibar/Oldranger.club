package ru.java.mentor.oldranger.club.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.java.mentor.oldranger.club.service.forum.SectionsAndTopicsService;


@Controller
@AllArgsConstructor
@RequestMapping("/test")
public class TestSearchController {

    private SectionsAndTopicsService sectionsAndTopicsService;

    @GetMapping("/search")
    public ModelAndView TestSearchPage(@RequestParam(value = "query", required = false) String query,
                                       @RequestParam(value = "searchBy", required = false) String searchBy) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("main");
        if (query != null && !query.trim().isEmpty())
            modelAndView.addObject("sectionsAndTopics", sectionsAndTopicsService.getSectionsAndTopicsByQuery(query, searchBy));
        return modelAndView;
    }
}