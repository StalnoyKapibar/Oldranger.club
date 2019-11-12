package ru.java.mentor.oldranger.club.service.forum.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.java.mentor.oldranger.club.dao.ForumRepository.TopicVisitAndSubscriptionRepository;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.forum.TopicVisitAndSubscription;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.forum.TopicVisitAndSubscriptionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopicVisitAndSubscriptionServiceImpl implements TopicVisitAndSubscriptionService {

    @Autowired
    private TopicVisitAndSubscriptionRepository topicVisitAndSubscriptionRepository;

    @Override
    public TopicVisitAndSubscription save(TopicVisitAndSubscription topicVisitAndSubscription) {
        return setHasNewMessages(topicVisitAndSubscriptionRepository.save(topicVisitAndSubscription));
    }

    @Override
    public TopicVisitAndSubscription subscribeUserOnTopic(User user, Topic topic) {
        TopicVisitAndSubscription topicVisitAndSubscription = getByUserAndTopic(user, topic);
        if (topicVisitAndSubscription == null) {
            topicVisitAndSubscription = new TopicVisitAndSubscription(user, topic, true);
            topicVisitAndSubscription.setSubscriptionTime(LocalDateTime.now());
        } else {
            topicVisitAndSubscription.setSubscribed(true);
        }
        return save(topicVisitAndSubscription);
    }

    @Override
    public TopicVisitAndSubscription unsubscribe(TopicVisitAndSubscription topicVisitAndSubscription) {
        topicVisitAndSubscription.setSubscribed(false);
        return topicVisitAndSubscriptionRepository.save(topicVisitAndSubscription);
    }

    @Override
    public TopicVisitAndSubscription unsubscribeUserFromTopic(User user, Topic topic) {
        TopicVisitAndSubscription topicVisitAndSubscription = getByUserAndTopic(user, topic);
        if (topicVisitAndSubscription == null) {
            topicVisitAndSubscription = new TopicVisitAndSubscription(user, topic, false);
        } else {
            topicVisitAndSubscription.setSubscribed(false);
        }
        return save(topicVisitAndSubscription);
    }

    @Override
    public TopicVisitAndSubscription updateVisitTime(TopicVisitAndSubscription topicVisitAndSubscription) {
        topicVisitAndSubscription.setLastVisitTime(LocalDateTime.now());
        return topicVisitAndSubscriptionRepository.save(topicVisitAndSubscription);
    }

    @Override
    @Transactional
    public TopicVisitAndSubscription updateVisitTime(User user, Topic topic) {
        return setHasNewMessages(topicVisitAndSubscriptionRepository.setVisitTime(user, topic, LocalDateTime.now()));
    }

    @Override
    public TopicVisitAndSubscription getByUserAndTopic(User user, Topic topic) {
        return setHasNewMessages(topicVisitAndSubscriptionRepository.getFirstByUserAndTopic(user, topic));
    }

    @Override
    public List<TopicVisitAndSubscription> getTopicVisitAndSubscriptionForUser(User user) {
        return setHasNewMessages(topicVisitAndSubscriptionRepository.getAllByUser(user));
    }

    @Override
    public List<TopicVisitAndSubscription> getTopicVisitAndSubscriptionForTopic(Topic topic) {
        return setHasNewMessages(topicVisitAndSubscriptionRepository.getAllByTopic(topic));
    }

    @Override
    public List<TopicVisitAndSubscription> getOnlySubscriptionsForTopic(Topic topic) {
        return setHasNewMessages(topicVisitAndSubscriptionRepository.getSubscriptionsByTopic(topic));
    }

    @Override
    public List<User> getUsersSubscribedOnTopic(Topic topic) {
        return getOnlySubscriptionsForTopic(topic).stream().map(TopicVisitAndSubscription::getUser).collect(Collectors.toList());
    }

    private TopicVisitAndSubscription setHasNewMessages(TopicVisitAndSubscription topicVisitAndSubscription) {
        if (topicVisitAndSubscription != null) {
            LocalDateTime lastMessageTime = topicVisitAndSubscription.getTopic().getLastMessageTime();
            LocalDateTime lastVisitTime = topicVisitAndSubscription.getLastVisitTime();
            if (lastMessageTime != null &&
                    (lastVisitTime == null || lastMessageTime.isAfter(lastVisitTime))) {
                topicVisitAndSubscription.setHasNewMessages(true);
            }
        }
        return topicVisitAndSubscription;
    }

    private List<TopicVisitAndSubscription> setHasNewMessages(List<TopicVisitAndSubscription> topicVisitAndSubscriptions) {
        for (TopicVisitAndSubscription topicVisitAndSubscription : topicVisitAndSubscriptions) {
            setHasNewMessages(topicVisitAndSubscription);
        }
        return topicVisitAndSubscriptions;
    }
}
