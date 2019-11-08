package ru.java.mentor.oldranger.club.service.chat.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ChatRepository.ChatRepository;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.service.chat.ChatService;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRepository chatRepository;


    @Override
    public Chat getChatById(Long id) {
        return chatRepository.findChatById(id);
    }

    @Override
    public void createChat(Chat chat) {
        chatRepository.save(chat);
    }

    @Override
    public void deleteChatById(Long id) {
    }

    @Override
    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }
}