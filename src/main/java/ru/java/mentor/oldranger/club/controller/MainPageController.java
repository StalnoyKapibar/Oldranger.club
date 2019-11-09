package ru.java.mentor.oldranger.club.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.java.mentor.oldranger.club.service.forum.SectionsAndTopicsService;

@Controller
@AllArgsConstructor
@RequestMapping("/")
public class MainPageController {

    private SectionsAndTopicsService sectionsAndTopicsService;

    @GetMapping
    public ModelAndView mainPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("main");
        modelAndView.addObject("sectionsAndTopics", sectionsAndTopicsService.getAllSectionsAndActualTopicsLimit10BySection());
        return modelAndView;
    }
}