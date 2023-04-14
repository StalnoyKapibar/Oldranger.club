package ru.oldranger.club.service.chat;

import ru.oldranger.club.model.chat.Chat;
import ru.oldranger.club.model.user.User;

import java.util.List;

public interface ChatService {

    Chat getChatById(Long id);

    Chat getChatByToken(String token);

    void createChat(Chat chat);

    List<Chat> getAllChats();

    List<Chat> getAllPrivateChats(User user);

    Chat getGroupChat();

    String generateToken(User first, User second);
}