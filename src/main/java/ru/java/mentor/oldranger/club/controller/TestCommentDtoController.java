package ru.java.mentor.oldranger.club.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.CommentDto;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.forum.TopicVisitAndSubscriptionService;

@Deprecated
@Hidden
@Controller
@AllArgsConstructor
public class TestCommentDtoController {

    private CommentService commentService;
    private TopicService topicService;
    private TopicVisitAndSubscriptionService topicVisitAndSubscriptionService;


    @RequestMapping(path = "/topic/{topicId}", method = RequestMethod.GET)
    public String getPageableComments(@PathVariable(value = "topicId") Long topicId,
                                      @SessionAttribute User currentUser,
                                      @RequestAttribute(value = "page", required = false) Integer page,
                                      @RequestParam(value = "pos",required = false) Integer position,
                                      @PageableDefault(size = 10, sort = "dateTime") Pageable pageable,
                                      Model model) {
        Topic topic = topicService.findById(topicId);
        if (topic == null) {
            model.addAttribute("message", "Такой темы еще не существует");
            return "404";
        }
        if (page != null) {
            pageable = PageRequest.of(page, 10, Sort.by("dateTime"));
        }
        if (position != null) {
            page = (position - 1 == 0) ? 0 : (position - 1) / pageable.getPageSize();
            pageable = PageRequest.of(page, 10, Sort.by("dateTime"));
        }
        Page<CommentDto> dtos = commentService.getPageableCommentDtoByTopic(topic, pageable);
        if (currentUser.getRole().getRole().equals("ROLE_PROSPECT")) {
            for (CommentDto cd : dtos.getContent()) {
                cd.getAuthor().getAvatar().setSmall("default-sm.png");
            }
        }
        topicVisitAndSubscriptionService.updateVisitTime(currentUser,topic);
        model.addAttribute("topic", topic);
        model.addAttribute("pageCount", dtos.getTotalPages());
        model.addAttribute("commentList", dtos.getContent());
        return "testCommentDtos";
    }
}