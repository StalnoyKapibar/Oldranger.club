package ru.java.mentor.oldranger.club.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.java.mentor.oldranger.club.dto.CommentDto;
import ru.java.mentor.oldranger.club.model.forum.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;

@Controller
@RequestMapping("/test*")
public class TestCommentDtoController {

    private CommentService commentService;
    private TopicService topicService;

    public TestCommentDtoController(CommentService commentService, TopicService topicService) {
        this.commentService = commentService;
        this.topicService = topicService;
    }

    @RequestMapping(path = "/comments/{topicId}", method = RequestMethod.GET)
    public String getPageableComments(@PathVariable(value = "topicId") Long topicId,
                                      @RequestAttribute(value = "page", required = false) Integer page,
                                      @PageableDefault(size = 10, sort = "dateTime") Pageable pageable,
                                      Model model) {
        Topic topic = topicService.findById(topicId);
        if (page != null) {
            pageable = PageRequest.of(page, 10, Sort.by("dateTime"));
        }
        Page<Comment> comments = commentService.getPageableCommentByTopic(topic, pageable);
        Page<CommentDto> dtos = comments.map(comment -> commentService.assembleCommentDto(comment));

        model.addAttribute("topic", topic);
        model.addAttribute("pageCount", dtos.getTotalPages());
        model.addAttribute("commentList", dtos.getContent());
        return "testCommentDtos";
    }

}
