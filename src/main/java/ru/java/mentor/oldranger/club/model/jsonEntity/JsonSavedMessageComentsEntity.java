package ru.java.mentor.oldranger.club.model.jsonEntity;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class JsonSavedMessageComentsEntity {

    private Long idTopic;
    private Long  idUser ;
    @NotEmpty
    @Pattern(regexp = "^(?=\\s*\\S).*$")
    private String text;
    private Long answerID;

    @Override
    public String toString() {
        return "JsonSavedMessageComentsEntity{" +
                "idTopic=" + idTopic +
                ", text='" + text + '\'' +
                ", answerID='" + answerID + '\'' +
                '}';
    }
}