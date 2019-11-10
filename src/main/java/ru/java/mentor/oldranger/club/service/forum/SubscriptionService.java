package ru.java.mentor.oldranger.club.service.forum;

import ru.java.mentor.oldranger.club.model.forum.Subscription;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.User;

import java.util.List;

public interface SubscriptionService {

    void subscribe(Subscription subscription);

    void subscribeUserOnTopic(User user, Topic topic);

    void unsubscribe(Subscription subscription);

    void unsubscribeUserFromTopic(User user, Topic topic);

    void updateVisitTime(Subscription subscription);

    void updateVisitTime(User user, Topic topic);

    List<Subscription> getSubscriptionsForUser(User user);

    List<Subscription> getSubscriptionsForTopic(Topic topic);

    List<User> getUsersSubscribedOnTopic(Topic topic);
}