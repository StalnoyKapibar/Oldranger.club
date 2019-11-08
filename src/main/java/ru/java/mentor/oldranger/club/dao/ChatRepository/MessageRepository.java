package ru.java.mentor.oldranger.club.dao.ChatRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.chat.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}