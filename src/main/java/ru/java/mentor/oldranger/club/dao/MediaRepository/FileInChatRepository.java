package ru.java.mentor.oldranger.club.dao.MediaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.media.FileInChat;

public interface FileInChatRepository extends JpaRepository<FileInChat, Long> {
}
