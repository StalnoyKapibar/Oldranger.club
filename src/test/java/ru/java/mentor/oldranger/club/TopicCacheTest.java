package ru.java.mentor.oldranger.club;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.user.UserService;

@SpringBootTest
public class TopicCacheTest {
    @Autowired
    private TopicService topicService;
    @Autowired
    private UserService userService;
    @Test
    public void topicTest() {
        topicService.findById(1L);
        topicService.findById(1L);
        topicService.deleteTopicById(1L);
        topicService.findById(1L);
        topicService.findAll();
        topicService.findAll();
        topicService.getActualTopicsLimit10();
        topicService.getActualTopicsLimit10();
        topicService.getActualTopicsLimit10BySection();
        topicService.getActualTopicsLimit10BySection();
        topicService.createTopic(new Topic());
        topicService.findAll();
        topicService.findAll();
        topicService.getActualTopicsLimit10BySection();
        topicService.getActualTopicsLimit10BySection();




    }
}
