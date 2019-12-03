package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@Tag(name = "Comments")
public class CommentRestController {

    private CommentService commentService;
    private TopicService topicService;
    private TopicVisitAndSubscriptionService topicVisitAndSubscriptionService;
    private SecurityUtilsService securityUtilsService;


    @Operation(security = @SecurityRequirement(name = "security"),
               summary = "Get CommentDto list", description = "Get comments by topic id", tags = { "Comments" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentDto.class)))),
            @ApiResponse(responseCode = "204", description = "invalid topic id")})
    @GetMapping(value = "/topic/{topicId}", produces = { "application/json" })
    public ResponseEntity<List<CommentDto>> getPageableComments(@PathVariable(value = "topicId") Long topicId,
                                                                @RequestParam(value = "page", required = false) Integer page,
                                                                @RequestParam(value = "pos",required = false) Integer position) {

        User currentUser = securityUtilsService.getLoggedUser();
        Topic topic = topicService.findById(topicId);
        if (topic == null) {
            return ResponseEntity.noContent().build();
        }

        if (page == null) page = 0;
        Pageable pageable = PageRequest.of(page, 10, Sort.by("dateTime"));

        if (position != null) {
            page = (position - 1 == 0) ? 0 : (position - 1) / pageable.getPageSize();
            pageable = PageRequest.of(page, 10, Sort.by("dateTime"));
        }
        List<CommentDto> dtos = commentService.getPageableCommentDtoByTopic(topic, pageable).getContent();
        topicVisitAndSubscriptionService.updateVisitTime(currentUser,topic);

        return ResponseEntity.ok(dtos);
    }
}
