package ru.java.mentor.oldranger.club.dao.ChatRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.dto.PrivateChatDto;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.chat.Message;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.Tuple;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAll();

    Chat findChatById(Long id);

    @Query(nativeQuery = true,
            value = "select * from chats where is_private = false LIMIT 1")
    Chat findGroupChat();

    Chat findChatByToken(String token);

    List<Chat> findAllByPrivacyTrueAndUserListContaining(User user);

    @Query(nativeQuery = true, value = "select t.id_chat, t.msg_text, message_date\n" +
            "from messages t\n" +
            "inner join (\n" +
            "    select id_chat, max(message_date) as MaxDate\n" +
            "    from messages\n" +
            "    group by id_chat\n" +
            ") tm on t.id_chat = tm.id_chat and t.message_date = tm.MaxDate")
    List<Tuple> getChatIdAndLastMessage();

}