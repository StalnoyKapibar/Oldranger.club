package ru.java.mentor.oldranger.club.service.forum;

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

    List<TopicVisitAndSubscription> getTopicVisitAndSubscriptionForTopic(Topic topic);

    List<TopicVisitAndSubscription> getOnlySubscriptionsForTopic(Topic topic);

    List<User> getUsersSubscribedOnTopic(Topic topic);
}
