package ru.java.mentor.oldranger.club.dao.ForumRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.forum.TopicVisitAndSubscription;
import ru.java.mentor.oldranger.club.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface TopicVisitAndSubscriptionRepository extends JpaRepository<TopicVisitAndSubscription, Long> {

    List<TopicVisitAndSubscription> getAllByUser(User user);

    List<TopicVisitAndSubscription> getAllByTopic(Topic topic);

    TopicVisitAndSubscription getFirstByUserAndTopic(User user, Topic topic);

    @Modifying
    @Query("update TopicVisitAndSubscription set lastVisitTime=:lastVisitTime where user=:user and topic=:topic")
    TopicVisitAndSubscription setVisitTime(User user, Topic topic, LocalDateTime lastVisitTime);


    @Query("select t from TopicVisitAndSubscription t where t.topic=:topic and t.isSubscribed=true")
    List<TopicVisitAndSubscription> getSubscriptionsByTopic(Topic topic);
}
