package ru.java.mentor.oldranger.club.service.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.chat.Message;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface MessageService {

    void addMessage(Message message);

    void editMessage(Message message);

    Page<Message> getPagebleMessages(Chat chat, Pageable pageable);

    Map<String, String> processImage(MultipartFile file);

    Map<String, Long> getOnlineUsers();

    void setOlderThan(Long olderThan);

    Message findFirstMessageByChat(Chat chat);

    void deleteMessages(boolean isPrivate, boolean deleteAll, String chatToken);

    Message findMessage(Long id);

    void deleteMessage(Long id);

    List<Message> findAllByChat(Chat chat);

    Message getLastMessage(Chat chat);

    List<Message> findAllByChatUnread(long chatId);

    HashMap<Long, Message> getAllChatsLastMessage(List<Chat> chats);

    HashMap<Long, Integer> getChatIdAndUnreadMessage(List<Chat> chats);
}