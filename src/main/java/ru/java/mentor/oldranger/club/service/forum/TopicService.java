package ru.java.mentor.oldranger.club.service.forum;

import ru.java.mentor.oldranger.club.model.forum.Topic;

import java.util.List;

public interface TopicService {

    void createTopic(Topic topic);

    void editTopicByName(Topic topic);

    void deleteTopicById(Long id);

    Topic findById(Long id);

    List<Topic> getActualTopicsLimitAnyBySection(Integer limitTopicsBySection);

    public void editTopicByName(Topic topic);

    List<Topic> getActualTopicsLimit10BySection();

    List<Topic> getActualTopicsLimit10BySectionForAnon();

}
