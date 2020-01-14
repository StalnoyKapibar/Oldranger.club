package ru.java.mentor.oldranger.club.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class CommentCreateAndUpdateDto {

    private Long idTopic;
    private Long  idUser ;
    @NotEmpty
    @Pattern(regexp = "^(?=\\s*\\S).*$")
    private String text;
    private Long answerID;

    @Override
    public String toString() {
        return "CommentCreateAndUpdateDto{" +
                "idTopic=" + idTopic +
                ", text='" + text + '\'' +
                ", answerID='" + answerID + '\'' +
                '}';
    }
}