package ru.java.mentor.oldranger.club.restcontroller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.CommentDto;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.forum.TopicVisitAndSubscriptionService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class CommentRestController {

    private CommentService commentService;
    private TopicService topicService;
    private TopicVisitAndSubscriptionService topicVisitAndSubscriptionService;
    private SecurityUtilsService securityUtilsService;


    @RequestMapping(path = "/topic/{topicId}", method = RequestMethod.GET)
    public ResponseEntity<List<CommentDto>> getPageableComments(@PathVariable(value = "topicId") Long topicId,
                                                    @RequestAttribute(value = "page", required = false) Integer page,
                                                    @RequestParam(value = "pos",required = false) Integer position,
                                                    @PageableDefault(size = 10, sort = "dateTime") Pageable pageable) {

        User currentUser = securityUtilsService.getLoggedUser();
        Topic topic = topicService.findById(topicId);
        if (topic == null) {
            return ResponseEntity.noContent().build();
        }
        if (page != null) {
            pageable = PageRequest.of(page, 10, Sort.by("dateTime"));
        }
        if (position != null) {
            page = (position - 1 == 0) ? 0 : (position - 1) / pageable.getPageSize();
            pageable = PageRequest.of(page, 10, Sort.by("dateTime"));
        }
        List<CommentDto> dtos = commentService.getPageableCommentDtoByTopic(topic, pageable).getContent();
        topicVisitAndSubscriptionService.updateVisitTime(currentUser,topic);

        return ResponseEntity.ok(dtos);
    }
}
