package ru.java.mentor.oldranger.club;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.java.mentor.oldranger.club.model.forum.Subscription;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.forum.SubscriptionService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.user.UserService;

import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SubscriptionTests {

    @Autowired
    private TopicService topicService;

    @Autowired
    private UserService userService;

    @Autowired
    private SubscriptionService subscriptionService;

    private final long SUBSCRIBER_USER_ID = 1L;

    @Test
    @Order(0)
    void subscribe() {
        List<Topic> topics = topicService.getActualTopicsLimit10BySection();
        User user = userService.findById(SUBSCRIBER_USER_ID);
        boolean b = true;
        for (Topic topic : topics) {
            subscriptionService.subscribeUserOnTopic(user, topic);
            if (b) {
                subscriptionService.updateVisitTime(user, topic);
                b = !b;
            }
        }
    }

    @Test
    @Order(10)
    void getSubscriptions() {
        User user = userService.findById(SUBSCRIBER_USER_ID);
        List<Subscription> userSubscriptions = subscriptionService.getSubscriptionsForUser(user);
        for (Subscription userSubscription : userSubscriptions) {
            System.out.println(userSubscription);
        }
    }

    @Test
    @Order(15)
    void getSubscribers() {
        List<Topic> topics = topicService.getActualTopicsLimit10BySection();
        for (Topic topic : topics) {
            System.out.println(topic);
            List<User> users = subscriptionService.getUsersSubscribedOnTopic(topic);
            for (User user : users) {
                System.out.println("\t" + user);
            }
        }

    }

    @Test
    @Order(20)
    void unsubscribe() {
        User user = userService.findById(SUBSCRIBER_USER_ID);
        List<Subscription> userSubscriptions = subscriptionService.getSubscriptionsForUser(user);
        for (Subscription userSubscription : userSubscriptions) {
//            subscriptionService.unsubscribe(userSubscription);
            subscriptionService.unsubscribeUserFromTopic(userSubscription.getUser(), userSubscription.getTopic());
        }
    }

    @Test
    @Order(30)
    void getSubscriptionsAfterUnsubsciption() {
        getSubscriptions();
    }
}