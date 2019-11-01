package ru.java.mentor.oldranger.club.service.forum;

import ru.java.mentor.oldranger.club.model.forum.Topic;

import java.util.List;

public interface TopicService {

    void createTopic(Topic topic);

    void editTopicByName(Topic topic);

    void deleteTopicById(Long id);

    List<Topic> getTopicsLimitAnyBySection(Integer limitTopicsBySection);

    List<Topic> getTopicsLimitAnyBySectionForAnon(Integer limitTopicsBySection);

    List<Topic> getTopicsLimit10BySection();

    List<Topic> getTopicsLimit10BySectionForAnon();

}
