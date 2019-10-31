package ru.java.mentor.oldranger.club.sevice.ForumService.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.TopicRepository;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.sevice.ForumService.TopicService;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicRepository topicRepository;

    @Override
    public void createTopic(Topic topic) {

    }

    @Override
    public void editTopicByName(Topic topic) {

    }

    @Override
    public void deleteTopicById(Long id) {

    }
}
