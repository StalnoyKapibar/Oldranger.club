package ru.java.mentor.oldranger.club.dto;


import lombok.*;
import ru.java.mentor.oldranger.club.model.user.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentDto {

    private long positionInArticle;
    private long articleId;
    private User author;
    private LocalDateTime commentDateTime;
    private Long messageCount;
    private LocalDateTime replyDateTime;
    private String replyNick;
    private String replyText;
    private String commentText;

}
