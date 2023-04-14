package ru.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.oldranger.club.dto.CommentDto;
import ru.oldranger.club.model.comment.Comment;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.model.utils.BanType;
import ru.oldranger.club.service.forum.CommentService;
import ru.oldranger.club.service.utils.SecurityUtilsService;
import ru.oldranger.club.service.utils.WritingBanService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class SystemCommentRestController {

    @Autowired
    private CommentService commentService;
    private WritingBanService writingBanService;
    private SecurityUtilsService securityUtilsService;

    @Operation(summary = "Get all comments in topic", tags = {"Topic comments"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentDto.class))))})
    @GetMapping("/com/comments/{id}")
    public ResponseEntity<List<CommentDto>> getComments(@SessionAttribute User currentUser, @PathVariable Long id) {
        List<Comment> commentsList;
        commentsList = commentService.getAllCommentsByTopicId(id);

        if (commentsList == null) {
            return ResponseEntity.noContent().build();
        }

        for (Comment comment : commentsList) {
            if (comment.getAnswerTo() != null) {
                comment.setPozition(true);
            }
        }
        return ResponseEntity.ok(commentsList.stream().map(a->commentService.assembleCommentDto(a, currentUser)).collect(Collectors.toList()));
    }

    @Operation(summary = "Is it forbidden to write comments", tags = {"Topic comments"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Boolean",
                    content = @Content(schema = @Schema(implementation = Boolean.class)))})
    @GetMapping("/com/isForbidden")
    public ResponseEntity<Boolean> isForbidden() {
        boolean isForbidden = writingBanService.isForbidden(securityUtilsService.getLoggedUser(), BanType.ON_COMMENTS);
        return ResponseEntity.ok(isForbidden);
    }
}