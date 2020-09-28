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
public class ArticleCommentDto extends ArticlePhotosDTO{

    private Long position;
    private Long id;
    private Long articleId;
    private User author;
    private LocalDateTime commentDateTime;
    private LocalDateTime replyDateTime;
    private Long parentId;
    private String replyNick;
    private String replyText;
    private String commentText;
    private boolean isDeleted;

    public ArticleCommentDto(Long position, Long id, Long articleId, User author, LocalDateTime commentDateTime, LocalDateTime replyDateTime, Long parentId, String replyNick, String replyText, String commentText, boolean isDeleted, List<Photo> photos) {
        this.position = position;
        this.id = id;
        this.articleId = articleId;
        this.author = author;
        this.commentDateTime = commentDateTime;
        this.replyDateTime = replyDateTime;
        this.parentId = parentId;
        this.replyNick = replyNick;
        this.replyText = replyText;
        this.commentText = commentText;
        this.isDeleted = isDeleted;
        setPhotos(photos);
    }
}
