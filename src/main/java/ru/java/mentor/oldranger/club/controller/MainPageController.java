package ru.java.mentor.oldranger.club.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.java.mentor.oldranger.club.service.forum.SectionsAndTopicsService;

@Hidden
@Controller
@AllArgsConstructor
@RequestMapping("/")
public class MainPageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainPageController.class);
    private SectionsAndTopicsService sectionsAndTopicsService;

    @GetMapping
    public ModelAndView mainPage() {
        LOGGER.info("Main page");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("main");
        modelAndView.addObject("sectionsAndTopics", sectionsAndTopicsService.getAllSectionsAndActualTopicsLimit10BySection());
        return modelAndView;
    }
}