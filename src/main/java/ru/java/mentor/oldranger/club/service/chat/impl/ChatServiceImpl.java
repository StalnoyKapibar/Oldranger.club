package ru.java.mentor.oldranger.club.service.chat.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ChatRepository.ChatRepository;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.service.chat.ChatService;

import java.util.List;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

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
    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }
}