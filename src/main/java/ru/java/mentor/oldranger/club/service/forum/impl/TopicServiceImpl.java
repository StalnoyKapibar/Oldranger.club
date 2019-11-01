package ru.java.mentor.oldranger.club.service.forum.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.TopicRepository;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.service.forum.TopicService;

import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {

    private TopicRepository topicRepository;

    @Autowired
    public void setTopicRepository(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Override
    public void createTopic(Topic topic) {
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
    public List<Topic> getTopicsLimitAnyBySection(Integer limitTopicsBySection) {
        return topicRepository.getTopicsLimitAnyBySection(limitTopicsBySection);
    }

    @Override
    public List<Topic> getTopicsLimitAnyBySectionForAnon(Integer limitTopicsBySection) {
        return topicRepository.getTopicsLimitAnyBySectionForAnon(limitTopicsBySection);
    }

    @Override
    public List<Topic> getTopicsLimit10BySection() {
        return topicRepository.getTopicsLimitAnyBySection(10);
    }

    @Override
    public List<Topic> getTopicsLimit10BySectionForAnon() {
        return topicRepository.getTopicsLimitAnyBySectionForAnon(10);
    }
}
