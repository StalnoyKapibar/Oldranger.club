package ru.java.mentor.oldranger.club.service.chat.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ChatRepository.MessageRepository;
import ru.java.mentor.oldranger.club.model.chat.Message;
import ru.java.mentor.oldranger.club.service.chat.MessageService;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public void addMessage(Message message) {
        messageRepository.save(message);
    }

    @Override
    public void removeMessageById(Long id) {
        messageRepository.deleteById(id);
    }
}