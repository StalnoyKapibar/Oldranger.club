package ru.java.mentor.oldranger.club.model.forum;

import ru.java.mentor.oldranger.club.model.user.User;

import java.time.LocalDateTime;

public class Comment {

    private Long id;

    private Topic topic;

    private User user;

    private Comment answerTo;

    private LocalDateTime dateTime;

    private String commentText;

    public Comment(Long id, Topic topic, User user, Comment answerTo, LocalDateTime dateTime, String commentText) {
        this.id = id;
        this.topic = topic;
        this.user = user;
        this.answerTo = answerTo;
        this.dateTime = dateTime;
        this.commentText = commentText;
    }
}
