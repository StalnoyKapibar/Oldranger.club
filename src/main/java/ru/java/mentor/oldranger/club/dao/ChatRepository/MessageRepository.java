package ru.java.mentor.oldranger.club.dao.ChatRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.chat.Message;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("select m from Message m where m.chat.id=:chatId and m.messageDate<=:date")
    List<Message> findAllByChatAndDate(long chatId, LocalDateTime date);

    Page<Message> findAllByChat(Chat chat, Pageable pageable);
}