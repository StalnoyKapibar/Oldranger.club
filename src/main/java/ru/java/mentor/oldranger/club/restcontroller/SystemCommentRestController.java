package ru.java.mentor.oldranger.club.restcontroller;

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