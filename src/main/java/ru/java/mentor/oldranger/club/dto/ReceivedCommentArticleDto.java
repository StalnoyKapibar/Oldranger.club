package ru.java.mentor.oldranger.club.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@Schema(description = "This is ReceivedCommentArticleDto,  where answerID - ID of comment on which answer was given ",
        requiredProperties = {"idUser", "idArticle"})
public class ReceivedCommentArticleDto {

    private Long idArticle;
    private Long idUser;
    @NotEmpty
    @Pattern(regexp = "^(?=\\s*\\S).*$")
    private String commentText;
    private Long answerId;

    @Override
    public String toString() {
        return "ReceivedCommentArticleDto{" +
                "idArticle=" + idArticle +
                ", idUser=" + idUser +
                ", commentText='" + commentText + '\'' +
                ", answerId=" + answerId +
                '}';
    }
}
