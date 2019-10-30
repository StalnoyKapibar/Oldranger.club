package ru.java.mentor.oldranger.club.model;

import java.time.LocalDateTime;

public class Comment {

    private Long id;

    private Topic topic;

    private User user;

    private Comment answerTo;

    private LocalDateTime dateTime;

    private String commentText;

}
