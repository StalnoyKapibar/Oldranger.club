package ru.java.mentor.oldranger.club.model.jsonEntity;

import lombok.Data;

@Data
public class JsonSavedMessageComentsEntity {

    private Long idTopic;
    private Long  idUser ;
    private String text;
    private Long answerID;

    public Long getIdTopic() {
        return idTopic;
    }

    public String getText() {
        return text;
    }

    public Long getAnswerID() {
        return answerID;
    }

    @Override
    public String toString() {
        return "JsonSavedMessageComentsEntity{" +
                "idTopic=" + idTopic +
                ", text='" + text + '\'' +
                ", answerID='" + answerID + '\'' +
                '}';
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }
}