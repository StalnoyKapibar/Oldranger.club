package ru.java.mentor.oldranger.club.service.chat;

import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.chat.Message;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.Tuple;
import java.util.HashMap;
import java.util.List;

public interface ChatService {

    Chat getChatById(Long id);

    Chat getChatByToken(String token);

    void createChat(Chat chat);

    List<Chat> getAllChats();

    List<Chat> getAllPrivateChats(User user);

    Chat getGroupChat();

    String generateToken(User first, User second);

    HashMap<Long, Message> getChatIdAndLastMessage(List<Chat> chats);
}