package ru.java.mentor.oldranger.club.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.java.mentor.oldranger.club.model.user.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentDto {

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

}
