package ru.java.mentor.oldranger.club.dao.ChatRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.dto.ArticleTagsNodeDto;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.chat.Message;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("select m from Message m where m.chat.id=:chatId and m.messageDate<=:date")
    List<Message> findAllByChatAndDate(long chatId, LocalDateTime date);

    Page<Message> findAllByChat(Chat chat, Pageable pageable);

    List<Message> findAllByChat(Chat chat);

    Message findFirstByChatOrderByMessageDateAsc(Chat chat);

    @Query("select m from Message m where m.chat.id=:chatId and m.isRead=false")
    List<Message> findAllByChatUnread(long chatId);

    Message findFirstByChatOrderByMessageDateDesc(Chat chat);

    @Query(nativeQuery = true,
            value = " SELECT id_chat, COUNT(is_reading) FROM messages where is_reading=false\n" +
                    "     GROUP BY id_chat")
    List<Tuple> getChatIdAndUnreadMessage();

}