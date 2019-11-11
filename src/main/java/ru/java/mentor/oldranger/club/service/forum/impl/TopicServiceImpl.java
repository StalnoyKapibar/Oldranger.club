package ru.java.mentor.oldranger.club.service.forum.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.TopicRepository;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import java.util.List;

@Service
@AllArgsConstructor
public class TopicServiceImpl implements TopicService {


    private TopicRepository topicRepository;
    private UserStatisticService userStatisticService;

    @Override
    public void createTopic(Topic topic) {
        UserStatistic userStatistic = userStatisticService.getUserStaticByUser(topic.getTopicStarter());
        long topicCount = userStatistic.getTopicStartCount();
        userStatistic.setTopicStartCount(++topicCount);
        userStatisticService.saveUserStatic(userStatistic);
        topicRepository.save(topic);
    }

    @Override
    public void editTopicByName(Topic topic) {
        topicRepository.save(topic);
    }

    @Override
    public void deleteTopicById(Long id) {
        topicRepository.deleteById(id);
    }

    @Override
    public Topic findById(Long id) {
        return topicRepository.findById(id).orElse(null);
    }

    @Override
    public List<Topic> getActualTopicsLimitAnyBySection(Integer limitTopicsBySection) {
        return topicRepository.getActualTopicsLimitAnyBySection(limitTopicsBySection);
    }

    @Override
    public List<Topic> getActualTopicsLimit10BySection() {
        return topicRepository.getActualTopicsLimitAnyBySection(10);
    }

    @Override
    public List<Topic> getActualTopicsLimit10BySectionForAnon() {
        return topicRepository.getActualTopicsLimitAnyBySectionForAnon(10);
    }

    @Override
    public List<Topic> getActualTopicsLimitAnyBySectionForAnon(int expecting_topics_limit_less_or_equals) {
        return topicRepository.getActualTopicsLimitAnyBySectionForAnon(expecting_topics_limit_less_or_equals);
    }
}