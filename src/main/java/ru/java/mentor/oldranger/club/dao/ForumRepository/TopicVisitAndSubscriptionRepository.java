package ru.java.mentor.oldranger.club.dao.ForumRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.forum.TopicVisitAndSubscription;
import ru.java.mentor.oldranger.club.model.user.User;

import java.util.List;

public interface TopicVisitAndSubscriptionRepository extends JpaRepository<TopicVisitAndSubscription, Long> {

    List<TopicVisitAndSubscription> getAllByUser(User user);

    List<TopicVisitAndSubscription> getAllByTopic(Topic topic);

    TopicVisitAndSubscription getFirstByUserAndTopic(User user, Topic topic);

    @Query("select t from TopicVisitAndSubscription t where t.topic=:topic and t.isSubscribed=true")
    List<TopicVisitAndSubscription> getSubscriptionsByTopic(Topic topic);


    @Query("select t.topic from TopicVisitAndSubscription t where t.user=:user and t.lastVisitTime<t.topic.lastMessageTime and t.isSubscribed=true")
    List<Topic> getSubscribedTopicsByUser(User user);
}
