package ru.java.mentor.oldranger.club.sevice.ForumService.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.TopicRepository;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.sevice.ForumService.TopicService;

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
    public List<Topic> get10ActualTopics() {
        return topicRepository.getFirst10ByOrderByLastMessageTimeDesc();
    }

    @Override
    public List<Topic> get10ActualTopicsForAnon() {
        return topicRepository.getActualTopicsForAnon(PageRequest.of(0, 10));
    }
}
