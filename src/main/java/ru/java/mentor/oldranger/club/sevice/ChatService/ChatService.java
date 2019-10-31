package ru.java.mentor.oldranger.club.sevice.ChatService;

import ru.java.mentor.oldranger.club.model.chat.Chat;

import java.util.List;

public interface ChatService {

    public Chat getChatByName(String name);

    public void createChat(Chat chat);

    public void deleteChatById(Long id);

    public List<Chat> getAllChats();
}
