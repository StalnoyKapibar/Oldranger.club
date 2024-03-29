package ru.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oldranger.club.model.media.Photo;
import ru.oldranger.club.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long commentId;
    private Long positionInTopic;
    private Long topicId;
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