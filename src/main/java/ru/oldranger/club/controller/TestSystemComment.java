package ru.oldranger.club.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.oldranger.club.dto.CommentCreateAndUpdateDto;
import ru.oldranger.club.model.comment.Comment;
import ru.oldranger.club.model.forum.Topic;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.model.utils.BanType;
import ru.oldranger.club.service.forum.CommentService;
import ru.oldranger.club.service.forum.TopicService;
import ru.oldranger.club.service.user.UserService;
import ru.oldranger.club.service.utils.SecurityUtilsService;
import ru.oldranger.club.service.utils.WritingBanService;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Deprecated
@Hidden
@AllArgsConstructor
@Controller
public class TestSystemComment {

    private CommentService commentService;
    private TopicService topicService;
    private UserService userService;
    private WritingBanService writingBanService;
    private SecurityUtilsService securityUtilsService;

    // Пример работы с системой комментирования
    @GetMapping("/com/{id}")
    public String sendComment(@PathVariable Long id,
                              Model model,
                              HttpSession session) {
        session.setAttribute("urlAva", "https://static.tolstoycomments.com/ui/38/73/c6/3873c649-85f1-492a-8f3a-c70de64aefbc.png");
        model.addAttribute("idTopic", id);
        List<Comment> commentsList = new ArrayList<>();
        commentsList = commentService.getAllCommentsByTopicId(id);
        for (Comment comment : commentsList) {
            if (comment.getAnswerTo() != null) {
                comment.setPozition(true);
            }
        }
        model.addAttribute("comments", commentsList);
        boolean isForbidden = false;
        User user = securityUtilsService.getLoggedUser();
        if (user != null) {
            session.setAttribute("nameUser", user.getNickName());
            session.setAttribute("idUser", user.getId());
            isForbidden = writingBanService.isForbidden(user, BanType.ON_COMMENTS);
        } else {
            isForbidden = true;
        }
        model.addAttribute("isForbidden", isForbidden);
        return "test-comment";
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "/com/save")
    public @ResponseBody
    String messageSave(@RequestBody CommentCreateAndUpdateDto message) {
        Comment comment = null;
        Topic topic = topicService.findById(message.getIdTopic());
        User user = userService.findById(message.getIdUser());
        LocalDateTime localDateTime = LocalDateTime.now();
        if (message.getAnswerID() != 0) {
            Comment answerComment = commentService.getCommentById(message.getAnswerID());
            comment = new Comment(topic, user, answerComment, localDateTime, message.getText());
        } else {
            comment = new Comment(topic, user, null, localDateTime, message.getText());
        }
        commentService.createComment(comment);
        return "ok";
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime joiningDate = LocalDateTime.parse("2014-04-01 00:00:00", formatter);
    }
}