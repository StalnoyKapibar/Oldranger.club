package ru.java.mentor.oldranger.club.service.forum.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.TopicRepository;
import ru.java.mentor.oldranger.club.model.forum.Topic;
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
    public List<Topic> findAll() {
        return topicRepository.findAll();
    }

    @Override
    public List<Topic> getActualTopicsLimitAnyBySection(Integer limitTopicsBySection) {
        return topicRepository.getActualTopicsLimitAnyBySection(limitTopicsBySection);
    }

    @Override
    public List<Topic> getActualTopicsLimitAnyBySectionForAnon(int expecting_topics_limit_less_or_equals) {
        return topicRepository.getActualTopicsLimitAnyBySectionForAnon(expecting_topics_limit_less_or_equals);
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
    public Page<Topic> getPageableBySubsection(Section subsection, Pageable pageable) {
        Page<Topic> page;
        if (securityUtilsService.isLoggedUserIsUser()) {
            page = getPageableBySubsectionForUser(securityUtilsService.getLoggedUser(), subsection, pageable);
        } else {
            page = getPageableBySubsectionForAnon(subsection, pageable);
        }

        return page;
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

    @Override
    public List<IdAndNumberProjection> getMessagesCountForTopics(List<Topic> topics) {
        List<Long> list = topics.stream().map(Topic::getId).collect(Collectors.toList());
        return topicRepository.getPairsTopicIdAndTotalMessagesCount(list);
    }

    @Override
    public List<IdAndNumberProjection> getNewMessagesCountForTopicsAndUser(List<Topic> topics, User user) {
        List<Long> list = topics.stream().map(Topic::getId).collect(Collectors.toList());
        return topicRepository.getPairsTopicIdAndNewMessagesCountForUserId(list, user.getId());
    }

    @Override
    public List<TopicAndNewMessagesCountDto> getTopicsDto(List<Topic> topics) {
        boolean logged = false;
        List<IdAndNumberProjection> newMessagesCountForTopicsAndUser = null;
        List<TopicVisitAndSubscription> topicVisitAndSubscriptionForUser = null;

        if (securityUtilsService.isLoggedUserIsUser()) {
            logged = true;
            User loggedUser = securityUtilsService.getLoggedUser();
            newMessagesCountForTopicsAndUser = getNewMessagesCountForTopicsAndUser(topics, loggedUser);
            topicVisitAndSubscriptionForUser = topicVisitAndSubscriptionService.getTopicVisitAndSubscriptionForUser(loggedUser);
        }

        List<IdAndNumberProjection> messagesCountForTopics = getMessagesCountForTopics(topics);

        List<TopicAndNewMessagesCountDto> dtos = new ArrayList<>();

        for (Topic topic : topics) {
            TopicAndNewMessagesCountDto dto = new TopicAndNewMessagesCountDto();
            dto.setTopic(topic);

            Optional<IdAndNumberProjection> messagesCountForTopic = messagesCountForTopics.stream().filter(t -> t.getId() == topic.getId()).findAny();
            if (messagesCountForTopic.isPresent()) {
                dto.setTotalMessages(messagesCountForTopic.get().getNumber());
            } else {
                dto.setTotalMessages(0);
            }

            if (logged) {
                boolean isSubscribed = topicVisitAndSubscriptionForUser.stream().filter(t -> t.getTopic().getId().equals(topic.getId())).anyMatch(TopicVisitAndSubscription::isSubscribed);
                dto.setIsSubscribed(isSubscribed);

                Optional<IdAndNumberProjection> newMessages = newMessagesCountForTopicsAndUser.stream().filter(t -> t.getId() == topic.getId()).findAny();

                if (newMessages.isPresent()) {
                    dto.setHasNewMessages(true);
                    dto.setNewMessagesCount(newMessages.get().getNumber());
                } else {
                    dto.setHasNewMessages(false);
                    dto.setNewMessagesCount(0L);
                }

            } else {
                dto.setIsSubscribed(null);
                dto.setHasNewMessages(null);
                dto.setNewMessagesCount(null);
            }

            dtos.add(dto);
        }

        return dtos;
    }
}
