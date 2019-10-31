package ru.java.mentor.oldranger.club.sevice.ChatService;

import ru.java.mentor.oldranger.club.model.chat.Message;

public interface MessageService {

    public void addMessage(Message message);

    public void removeMessageById(Long id);

}
