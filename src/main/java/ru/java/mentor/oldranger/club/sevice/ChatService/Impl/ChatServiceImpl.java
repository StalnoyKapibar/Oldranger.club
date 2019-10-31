package ru.java.mentor.oldranger.club.sevice.ChatService.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ChatRepository.ChatRepository;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.sevice.ChatService.ChatService;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Override
    public Chat getChat() {
        return null;
    }

    @Override
    public void createChat(Chat chat) {
    }

    @Override
    public void deleteChat() {
    }

    @Override
    public List<Chat> getAllChats() {
        return null;
    }
}
