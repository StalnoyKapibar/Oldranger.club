package ru.java.mentor.oldranger.club.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.forum.SectionService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.user.UserService;

@Controller
@AllArgsConstructor
@RequestMapping("/test")
public class TestScrollableTopicsController {

    private TopicService topicService;

    private SectionService sectionService;

    private UserService userService;

    @GetMapping("/subsection/{subsectionId}")
    public ModelAndView getContainer(@PathVariable long subsectionId) {
        ModelAndView modelAndView = new ModelAndView("scrollabletopics/container");
        return modelAndView;
    }

    @PostMapping("/subsection/{subsectionId}")
    public ModelAndView getContainer(@PathVariable long subsectionId, Pageable pageable) {
        ModelAndView modelAndView = new ModelAndView("scrollabletopics/part");

        Section section = sectionService.getById(subsectionId).get();
        User user = userService.getUserByNickName("admin");
        Page<Topic> pageableTopicsBySubsection = topicService.getPageableBySubsectionForUser(user, section, pageable);

        modelAndView.addObject("page", pageable.getPageNumber());
        modelAndView.addObject("topics", pageableTopicsBySubsection.getContent());
        return modelAndView;
    }
}
