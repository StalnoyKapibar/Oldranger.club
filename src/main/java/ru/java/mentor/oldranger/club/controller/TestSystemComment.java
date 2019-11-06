package ru.java.mentor.oldranger.club.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.model.forum.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.jsonEntity.JsonSavedMessageComentsEntity;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.forum.SectionService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.user.UserService;

import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TestSystemComment {

    @Autowired
    CommentService commentService;

    @Autowired
    TopicService topicService;

    @Autowired
    UserService userService;

    @Autowired
    SectionService sectionService;

    // Пример работы с системой комментирования
    @GetMapping("/com/{id}")
    public String sendComment(
            @PathVariable Long id,
            Model model,
            HttpSession session
    ) {
        session.setAttribute("nameUser", "Admin");
        session.setAttribute("urlAva", "https://static.tolstoycomments.com/ui/38/73/c6/3873c649-85f1-492a-8f3a-c70de64aefbc.png");
        model.addAttribute("idTopic", id);
        List<Comment> commentsList = new ArrayList<>();
        commentsList = commentService.getAllCommentsByTopicId(id);
        for(Comment comment: commentsList){
            if(comment.getAnswerTo() != null){
                comment.setPozition(true);
            }
        }
        model.addAttribute("comments", commentsList);
        return "test-comment";
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "/com/save")
    public @ResponseBody
    String messageSave(
            @RequestBody JsonSavedMessageComentsEntity message
    ) {
        Comment comment = null;
        Topic topic = topicService.findById(message.getIdTopic());
        User user = userService.getUserByNickName(message.getName());
        Instant instant = Instant.ofEpochMilli(message.getTime());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        if (message.getAnswerName() != "") {
            Instant instantAnswer = Instant.ofEpochMilli(message.getAnswerTime());
            LocalDateTime localDateTimeAnswer = LocalDateTime.ofInstant(instantAnswer, ZoneId.systemDefault());
            Comment answerComment = commentService.getCommentAnswer(message.getAnswerName(), localDateTimeAnswer);
            comment = new Comment(topic, user, answerComment, localDateTime, String.valueOf(message.getText()));
        } else {
            comment = new Comment(topic, user, null, localDateTime, message.getText());
        }
        commentService.createComment(comment);
        return "ok";
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime joiningDate = LocalDateTime.parse("2014-04-01 00:00:00", formatter);
    }
}