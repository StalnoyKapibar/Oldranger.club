package ru.java.mentor.oldranger.club.service.media;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.model.media.FileInChat;

import java.util.List;

public interface FileInChatService {
    FileInChat save(MultipartFile file, Long chatId);
    List<FileInChat> findAllByChat (Long chatId);
    FileInChat findByName(String fileName);
    void deleteAllByChat(Long chatId);

}
