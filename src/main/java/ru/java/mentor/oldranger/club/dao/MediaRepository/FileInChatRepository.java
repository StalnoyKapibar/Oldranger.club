package ru.java.mentor.oldranger.club.dao.MediaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.media.FileInChat;

import java.util.List;

public interface FileInChatRepository extends JpaRepository<FileInChat, Long> {
    List<FileInChat> findByChatID(Long chatId);
    void deleteAllByChatID(Long chatId);
    FileInChat findByFileName(String fileName);
    void deleteByFileName(String fileName);
}
