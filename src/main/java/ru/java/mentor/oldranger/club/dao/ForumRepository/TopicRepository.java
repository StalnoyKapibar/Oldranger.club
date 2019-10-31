package ru.java.mentor.oldranger.club.dao.ForumRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.model.forum.Topic;

import java.awt.print.Pageable;
import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> getFirst10ByOrderByLastMessageTimeDesc();

    @Query("SELECT t FROM Topic t WHERE t.isHideToAnon = false ORDER BY t.lastMessageTime DESC")
    List<Topic> getActualTopicsForAnon(PageRequest pageable);
}
