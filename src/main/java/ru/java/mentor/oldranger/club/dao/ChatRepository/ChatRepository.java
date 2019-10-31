package ru.java.mentor.oldranger.club.dao.ChatRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.chat.Chat;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAll();

    Chat findChatById(Long id);

}