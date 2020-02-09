package ru.java.mentor.oldranger.club.dto;

import lombok.*;
import ru.java.mentor.oldranger.club.model.forum.ImageComment;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private long commentId;
    private long positionInTopic;
    private long topicId;
    private User author;
    private LocalDateTime commentDateTime;
    private Long messageCount;
    private LocalDateTime replyDateTime;
    private String replyNick;
    private String replyText;
    private String commentText;
    private List<Photo> photos;
    private boolean updatable;
}