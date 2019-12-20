package ru.java.mentor.oldranger.club.dao.ChatRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.model.chat.Chat;
import ru.java.mentor.oldranger.club.model.user.User;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAll();

    Chat findChatById(Long id);

    Chat findChatByToken(String token);

    List<Chat> findAllByPrivacyTrueAndUserListContaining(User user);
    @Query(nativeQuery = true,
            value = "select count(*) from chat_user c where c.user_id = :userID")
    Integer getCountChatByUserID(Long userID);

}