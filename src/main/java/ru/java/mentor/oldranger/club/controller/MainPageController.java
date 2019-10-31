package ru.java.mentor.oldranger.club.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.java.mentor.oldranger.club.dto.SectionsAndTopicsDto;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.sevice.ForumService.SectionService;
import ru.java.mentor.oldranger.club.sevice.ForumService.TopicService;

import java.util.Collection;

@Controller
@RequestMapping("/")
public class MainPageController {

    private RoleHierarchy roleHierarchy;

    private SectionService sectionService;

    private TopicService topicService;

    public MainPageController(RoleHierarchy roleHierarchy, SectionService sectionService, TopicService topicService) {
        this.roleHierarchy = roleHierarchy;
        this.sectionService = sectionService;
        this.topicService = topicService;
    }

    @GetMapping
    public ModelAndView mainPage(Authentication authentication) {

        Collection<? extends GrantedAuthority> reachableGrantedAuthorities = roleHierarchy.getReachableGrantedAuthorities(authentication.getAuthorities());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("main");
        SectionsAndTopicsDto sectionsAndTopicsDto;

        if (reachableGrantedAuthorities.contains(new Role("ROLE_USER"))) {
            sectionsAndTopicsDto = new SectionsAndTopicsDto(
                    sectionService.getAllSections(),
                    topicService.get10ActualTopics()
            );
        } else {
            sectionsAndTopicsDto = new SectionsAndTopicsDto(
                    sectionService.getAllSectionsForAnon(),
                    topicService.get10ActualTopicsForAnon()
            );
        }

        modelAndView.addObject("sectionsAndTopics", sectionsAndTopicsDto);
        return modelAndView;
    }
}
