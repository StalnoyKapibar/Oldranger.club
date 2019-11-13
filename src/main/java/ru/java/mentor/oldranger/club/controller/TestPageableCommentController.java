package ru.java.mentor.oldranger.club.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
import ru.java.mentor.oldranger.club.model.forum.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

@Controller
public class TestPageableCommentController {

    private CommentService commentService;
    private TopicService topicService;
    private SecurityUtilsService securityUtilsService;

    @Autowired
    public TestPageableCommentController(CommentService commentService,
                                         TopicService topicService,
                                         SecurityUtilsService securityUtilsService) {
        this.commentService = commentService;
        this.topicService = topicService;
        this.securityUtilsService = securityUtilsService;
    }

    @RequestMapping(path = "/test/topic/{topicId}", method = RequestMethod.GET)
    public String getPageableComments(@PathVariable(value = "topicId") Long topicId,
                                      @RequestAttribute(value = "page", required = false) Integer page,
                                      @PageableDefault(size = 10, sort = "dateTime") Pageable pageable,
                                      Model model) {
        Topic topic = topicService.findById(topicId);
        if (page != null) {
            pageable = PageRequest.of(page, 10, Sort.by("dateTime"));
        }
        Page<Comment> commentList = commentService.getPageableCommentByTopic(topic, pageable);

        User user = securityUtilsService.getLoggedUser();

        model.addAttribute("topic", topic);
        model.addAttribute("pageCount", commentList.getTotalPages());
        model.addAttribute("commentList", commentList.getContent());
        model.addAttribute("user", user);
        return "testPageable";
    }
}