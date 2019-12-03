package ru.java.mentor.oldranger.club.dao.ChatRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.user.User;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAll();

    Chat findChatById(Long id);

    @Query(nativeQuery = true,
            value = "select * from (select * from chats cId left join from (" +
            "select * from chat_users ON cId.id = chat_users.chat_id)) where user_id = :userId")
    List<Chat> getChatByUserListContaining(Long userID);

}