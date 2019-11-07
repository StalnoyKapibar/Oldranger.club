package ru.java.mentor.oldranger.club.service.forum.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.TopicRepository;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.forum.TopicService;

import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicRepository topicRepository;

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
    public Topic findById(Long id) {
        return topicRepository.findById(id).orElse(null);
    }

    @Override
    public List<Topic> getActualTopicsLimitAnyBySection(Integer limitTopicsBySection) {
        return topicRepository.getActualTopicsLimitAnyBySection(limitTopicsBySection);
    }

    @Override
    public List<Topic> getActualTopicsLimitAnyBySectionForAnon(Integer limitTopicsBySection) {
        return topicRepository.getActualTopicsLimitAnyBySectionForAnon(limitTopicsBySection);
    }

    @Override
    public List<Topic> getActualTopicsLimit10BySection() {
        return topicRepository.getActualTopicsLimitAnyBySection(10);
    }

    @Override
    public List<Topic> getActualTopicsLimit10BySectionForAnon() {
        return topicRepository.getActualTopicsLimitAnyBySectionForAnon(10);
    }

    public Page<Topic> getPageableBySubsectionForAnon(Section section, Pageable pageable) {
        return topicRepository.findBySectionAndIsHideToAnonIsFalseOrderByLastMessageTimeDesc(section, pageable);
    }

    @Override
    public Page<Topic> getPageableBySubsectionForUser(User user, Section subsection, Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int offset = pageNumber * pageSize;
        return new PageImpl<>(
                topicRepository.getSliceListBySubsectionForUserOrderByLastMessageTimeDescAndSubscriptionsWithNewMessagesFirst(user.getId(), subsection.getId(), offset, pageSize),
                pageable,
                topicRepository.countForGetSliceListBySubsectionForUserOrderByLastMessageTimeDescAndSubscriptionsWithNewMessagesFirst(user.getId(), subsection.getId())
        );
    }
}
