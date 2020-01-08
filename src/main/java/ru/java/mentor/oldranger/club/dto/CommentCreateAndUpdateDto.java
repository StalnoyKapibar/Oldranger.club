package ru.java.mentor.oldranger.club.dto;

import lombok.Data;

@Data
public class CommentCreateAndUpdateDto {

    private Long idTopic;
    private Long  idUser ;
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