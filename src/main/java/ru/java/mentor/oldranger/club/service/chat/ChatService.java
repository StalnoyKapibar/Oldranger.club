package ru.java.mentor.oldranger.club.service.chat;

import ru.java.mentor.oldranger.club.model.chat.Chat;

import java.util.List;

public interface ChatService {

    Chat getChatById(Long id);

    void createChat(Chat chat);

    List<Chat> getAllChats();
}