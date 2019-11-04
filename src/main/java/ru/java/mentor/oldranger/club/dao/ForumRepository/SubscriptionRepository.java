package ru.java.mentor.oldranger.club.dao.ForumRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.model.forum.Subscription;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> getAllByUser(User user);

    List<Subscription> getAllByTopic(Topic topic);

    @Modifying
    @Query("delete from Subscription where user=:user and topic=:topic")
    void unsubscribe(User user, Topic topic);

    @Modifying
    @Query("update Subscription set lastVisitTime=:lastVisitTime where user=:user and topic=:topic")
    void setVisitTime(User user, Topic topic, LocalDateTime lastVisitTime);

}
