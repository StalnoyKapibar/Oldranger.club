package ru.java.mentor.oldranger.club.model.forum;

import ru.java.mentor.oldranger.club.model.user.User;

import java.time.LocalDateTime;

public class Topic {

    private Long id;

    private String name;

    private User topicStarter;

    private LocalDateTime startTime;

    private LocalDateTime lastMessageTime;

    private Section section;

    private boolean isHideToAnon;

}
