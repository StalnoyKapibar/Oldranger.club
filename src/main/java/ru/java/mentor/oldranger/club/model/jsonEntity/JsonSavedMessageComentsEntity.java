package ru.java.mentor.oldranger.club.model.jsonEntity;

import lombok.Data;

@Data
public class JsonSavedMessageComentsEntity {

    private Long idTopic;
    private Long  idUser ;
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