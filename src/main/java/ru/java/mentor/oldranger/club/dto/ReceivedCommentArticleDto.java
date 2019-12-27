package ru.java.mentor.oldranger.club.dto;


import lombok.*;

@Data
@AllArgsConstructor
public class ReceivedCommentArticleDto {

    private Long idArticle;
    private Long idUser;
    private String commentText;
    private Long answerId;
}
