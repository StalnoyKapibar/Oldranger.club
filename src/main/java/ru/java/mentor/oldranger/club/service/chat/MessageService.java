package ru.java.mentor.oldranger.club.service.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.dto.MessageDTO;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.chat.Message;

import java.util.Map;

public interface MessageService {

    void addMessage(Message message);

    void editMessage(Message message);

    Page<Message> getPagebleMessages(Chat chat, Pageable pageable);

    Map<String, String> processImage(MultipartFile file);

    Map<String, Long> getOnlineUsers();

    void setOlderThan(String olderThan);

    Message findFirstMessageByChat(Chat chat);

    void deleteMessages(boolean isPrivate, boolean deleteAll, String chatToken);

    Message findMessage(Long id);

    void deleteMessage(Long id);

    MessageDTO setMessageDTO(Message message);
}