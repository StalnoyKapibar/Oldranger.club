package ru.java.mentor.oldranger.club.service.chat;

import ru.java.mentor.oldranger.club.model.chat.Chat;

import java.util.List;

public interface ChatService {

    public Chat getChatById(Long id);

    public void createChat(Chat chat);

    public void deleteChatById(Long id);

    public List<Chat> getAllChats();
}