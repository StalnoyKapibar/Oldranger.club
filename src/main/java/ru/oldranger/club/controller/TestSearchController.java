package ru.oldranger.club.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.oldranger.club.service.forum.SectionsAndTopicsService;

@Hidden
@Controller
@AllArgsConstructor
@RequestMapping("/test")
@Deprecated
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