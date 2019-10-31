package ru.java.mentor.oldranger.club.sevice.ChatService;

import ru.java.mentor.oldranger.club.model.chat.Chat;

import java.util.List;

public interface ChatService {

    public Chat getChat();

    public void createChat();

    public void deleteChat();

    public List<Chat> getAllChats();
}
