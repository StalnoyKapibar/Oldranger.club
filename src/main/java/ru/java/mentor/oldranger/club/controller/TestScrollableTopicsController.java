package ru.java.mentor.oldranger.club.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.java.mentor.oldranger.club.model.forum.Subsection;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.service.forum.SubsectionService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;

import java.util.Optional;
@Deprecated
@Hidden
@Controller
@AllArgsConstructor
@RequestMapping("/test")
public class TestScrollableTopicsController {

    private TopicService topicService;

    private SubsectionService subsectionService;

    @GetMapping("/subsection/{subsectionId}")
    public ModelAndView getContainer(@PathVariable long subsectionId) {
        ModelAndView modelAndView = new ModelAndView("scrollabletopics/container");

        Optional<Subsection> optionalSubsection = subsectionService.getById(subsectionId);

        if (!optionalSubsection.isPresent()) {
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            return modelAndView;
        }

        modelAndView.addObject("next_page_link", "?page=0");
        return modelAndView;
    }

    @PostMapping("/subsection/{subsectionId}")
    public ModelAndView getPart(@PathVariable long subsectionId,
                                @PageableDefault(size = 20) Pageable pageable) {
        ModelAndView modelAndView = new ModelAndView("scrollabletopics/part");

        Optional<Subsection> optionalSubsection = subsectionService.getById(subsectionId);

        if (!optionalSubsection.isPresent()) {
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            return modelAndView;
        }

        Subsection subsection = optionalSubsection.get();

        Page<Topic> pageableTopicsBySubsection = topicService.getPageableBySubsection(subsection, pageable);

        if (pageableTopicsBySubsection.getNumberOfElements() == 0) {
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            return modelAndView;
        }

        int currentPageNumber = pageable.getPageNumber();
        String nextPageLink = null;

        if (pageableTopicsBySubsection.getTotalPages() > currentPageNumber && !pageableTopicsBySubsection.isLast()) {
            nextPageLink = "?page=" + (currentPageNumber + 1);
        }

        modelAndView.addObject("page_number", currentPageNumber);
        modelAndView.addObject("next_page_link", nextPageLink);
//        modelAndView.addObject("topics", pageableTopicsBySubsection.getContent());
        modelAndView.addObject("topics_dto", topicService.getTopicsDto(pageableTopicsBySubsection.getContent()));
        return modelAndView;
    }
}