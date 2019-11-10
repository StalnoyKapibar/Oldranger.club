package ru.java.mentor.oldranger.club.service.forum.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.java.mentor.oldranger.club.dao.ForumRepository.SubscriptionRepository;
import ru.java.mentor.oldranger.club.model.forum.Subscription;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.forum.SubscriptionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Override
    public void subscribe(Subscription subscription) {
        subscriptionRepository.save(subscription);
    }

    @Override
    public void subscribeUserOnTopic(User user, Topic topic) {
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setTopic(topic);
        subscription.setSubscriptionTime(LocalDateTime.now());
        subscribe(subscription);
    }

    @Override
    public void unsubscribe(Subscription subscription) {
        subscriptionRepository.delete(subscription);
    }

    @Override
    @Transactional
    public void unsubscribeUserFromTopic(User user, Topic topic) {
        subscriptionRepository.unsubscribe(user, topic);
    }

    @Override
    public void updateVisitTime(Subscription subscription) {
        subscription.setLastVisitTime(LocalDateTime.now());
        subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public void updateVisitTime(User user, Topic topic) {
        subscriptionRepository.setVisitTime(user, topic, LocalDateTime.now());
    }

    @Override
    public List<Subscription> getSubscriptionsForUser(User user) {
        List<Subscription> subscriptions = subscriptionRepository.getAllByUser(user);
        return checkNewMessages(subscriptions);
    }

    @Override
    public List<Subscription> getSubscriptionsForTopic(Topic topic) {
        List<Subscription> subscriptions = subscriptionRepository.getAllByTopic(topic);
        return checkNewMessages(subscriptions);
    }

    @Override
    public List<User> getUsersSubscribedOnTopic(Topic topic) {
        List<Subscription> topicSubscriptions = getSubscriptionsForTopic(topic);
        return topicSubscriptions.stream().map(Subscription::getUser).collect(Collectors.toList());
    }

    private List<Subscription> checkNewMessages(List<Subscription> subscriptions) {
        for (Subscription subscription : subscriptions) {
            LocalDateTime lastMessageTime = subscription.getTopic().getLastMessageTime();
            LocalDateTime lastVisitTime = subscription.getLastVisitTime();
            if (lastVisitTime == null || lastMessageTime.isAfter(lastVisitTime)) {
                subscription.setHasNewMessages(true);
            }
        }
        return subscriptions;
    }
}