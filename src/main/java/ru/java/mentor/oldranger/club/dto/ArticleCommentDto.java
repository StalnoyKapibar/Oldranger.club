package ru.java.mentor.oldranger.club.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentDto {

    private Long id;
    private Long position;
    private Long articleId;
    private String articleName;
    private User author;
    private LocalDateTime commentDateTime;
    private LocalDateTime commentUpdateTime;
    private Long messageCount;
    private LocalDateTime replyDateTime;
    private Long parentId;
    private String replyNick;
    private String replyText;
    private String commentText;
    private List<Photo> photos;
    private boolean updatable;
    private boolean isDeleted;
}
