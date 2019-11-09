package ru.java.mentor.oldranger.club.service.chat;

import ru.java.mentor.oldranger.club.model.chat.Message;

public interface MessageService {

    public void addMessage(Message message);

    public void removeMessageById(Long id);
}