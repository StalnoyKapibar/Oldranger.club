package ru.java.mentor.oldranger.club.service.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.chat.Message;

import java.io.IOException;
import java.util.Map;

public interface MessageService {

    void addMessage(Message message);

    void removeMessageById(Long id);

    Page<Message> getPagebleMessages(Chat chat, Pageable pageable);

    Map<String,String> processImage(MultipartFile file) throws IOException;
}