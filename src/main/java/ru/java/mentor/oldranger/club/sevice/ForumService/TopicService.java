package ru.java.mentor.oldranger.club.sevice.ForumService;

import ru.java.mentor.oldranger.club.model.forum.Topic;

import java.util.List;

public interface TopicService {

    void createTopic(Topic topic);

    void editTopicByName(Topic topic);

    void deleteTopicById(Long id);

    List<Topic> get10ActualTopics();

    List<Topic> get10ActualTopicsForAnon();
}
