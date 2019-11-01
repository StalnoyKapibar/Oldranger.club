package ru.java.mentor.oldranger.club.service.forum;

import ru.java.mentor.oldranger.club.model.forum.Topic;

import java.util.List;

public interface TopicService {

    void createTopic(Topic topic);

    void editTopicByName(Topic topic);

    void deleteTopicById(Long id);

    List<Topic> getActualTopicsLimitAnyBySection(Integer limitTopicsBySection);

    List<Topic> getActualTopicsLimitAnyBySectionForAnon(Integer limitTopicsBySection);

    List<Topic> getActualTopicsLimit10BySection();

    List<Topic> getActualTopicsLimit10BySectionForAnon();

}
