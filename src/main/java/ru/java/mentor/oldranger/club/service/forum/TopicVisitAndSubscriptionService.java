package ru.java.mentor.oldranger.club.service.forum;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.forum.TopicVisitAndSubscription;
import ru.java.mentor.oldranger.club.model.user.User;

import java.util.List;

public interface TopicVisitAndSubscriptionService {

    TopicVisitAndSubscription save(TopicVisitAndSubscription topicVisitAndSubscription);

    TopicVisitAndSubscription subscribeUserOnTopic(User user, Topic topic);

    TopicVisitAndSubscription unsubscribe(TopicVisitAndSubscription topicVisitAndSubscription);

    TopicVisitAndSubscription unsubscribeUserFromTopic(User user, Topic topic);

    TopicVisitAndSubscription updateVisitTime(TopicVisitAndSubscription topicVisitAndSubscription);

    TopicVisitAndSubscription updateVisitTime(User user, Topic topic);

    TopicVisitAndSubscription getByUserAndTopic(User user, Topic topic);

    List<TopicVisitAndSubscription> getTopicVisitAndSubscriptionForUser(User user);

    Page<Topic> getPagebleSubscribedTopicsForUser(User user, Pageable pageable);

    Page<TopicVisitAndSubscription> getPagebleTopicVisitAndSubscriptionForUser(User user, Pageable pageable);

    List<TopicVisitAndSubscription> getTopicVisitAndSubscriptionForTopic(Topic topic);

    List<TopicVisitAndSubscription> getOnlySubscriptionsForTopic(Topic topic);

    List<User> getUsersSubscribedOnTopic(Topic topic);
}
