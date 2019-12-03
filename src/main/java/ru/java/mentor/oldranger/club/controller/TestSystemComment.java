package ru.java.mentor.oldranger.club.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.model.forum.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.jsonEntity.JsonSavedMessageComentsEntity;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BanType;
import ru.java.mentor.oldranger.club.model.utils.WritingBan;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.WritingBanService;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TestSystemComment {

    private CommentService commentService;
    private TopicService topicService;
    private UserService userService;
    private WritingBanService writingBanService;

    @Autowired
    public TestSystemComment(CommentService commentService, TopicService topicService, UserService userService, WritingBanService writingBanService) {
        this.commentService = commentService;
        this.topicService = topicService;
        this.userService = userService;
        this.writingBanService = writingBanService;
    }

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
        try {
            UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            session.setAttribute("nameUser", user.getNickName());
            session.setAttribute("idUser", user.getId());
            WritingBan writingBan = writingBanService.getByUserAndType(user, BanType.ON_COMMENTS);
            if (writingBan != null && (writingBan.getUnlockTime()==null || writingBan.getUnlockTime().isAfter(LocalDateTime.now()))){
                isForbidden = true;
            }
        }
        catch (Exception e) {
            isForbidden = true;
        }
        model.addAttribute("isForbidden", isForbidden);
        return "test-comment";
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "/com/save")
    public @ResponseBody
    String messageSave(@RequestBody JsonSavedMessageComentsEntity message) {
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
        commentService.createComment(comment, topic);
        return "ok";
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime joiningDate = LocalDateTime.parse("2014-04-01 00:00:00", formatter);
    }
}