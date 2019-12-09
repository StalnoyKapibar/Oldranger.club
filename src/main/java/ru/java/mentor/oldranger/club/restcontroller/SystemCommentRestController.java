package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.java.mentor.oldranger.club.dto.CommentDto;
import ru.java.mentor.oldranger.club.model.forum.Comment;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BanType;
import ru.java.mentor.oldranger.club.model.utils.WritingBan;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.utils.WritingBanService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class SystemCommentRestController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private WritingBanService writingBanService;

    @Operation(summary = "Get all comments in topic", tags = { "Topic comments" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentDto.class))))})
    @GetMapping("/com/comments/{id}")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long id) {
        List<Comment> commentsList = new ArrayList<>();
        commentsList = commentService.getAllCommentsByTopicId(id);
        for (Comment comment : commentsList) {
            if (comment.getAnswerTo() != null) {
                comment.setPozition(true);
            }
        }
        return ResponseEntity.ok(commentsList.stream().map(commentService::assembleCommentDto).collect(Collectors.toList()));
    }

    @Operation(summary = "Can a user participate", tags = { "Topic comments" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Boolean",
                    content = @Content(schema = @Schema(implementation = Boolean.class)))})
    @GetMapping("/com/status/{id}")
    public ResponseEntity<Boolean> getStatus() {
        boolean isForbidden = false;
        try {
            UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            WritingBan writingBan = writingBanService.getByUserAndType(user, BanType.ON_COMMENTS);
            if (writingBan != null && (writingBan.getUnlockTime()==null || writingBan.getUnlockTime().isAfter(LocalDateTime.now()))){
                isForbidden = true;
            }
        }
        catch (Exception e) {
            isForbidden = true;
        }
        return ResponseEntity.ok(isForbidden);
    }
}