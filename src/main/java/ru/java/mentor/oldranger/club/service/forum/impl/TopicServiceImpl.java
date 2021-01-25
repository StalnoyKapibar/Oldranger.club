package ru.java.mentor.oldranger.club.service.forum.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.TopicRepository;
import ru.java.mentor.oldranger.club.dto.TopicAndNewMessagesCountDto;
import ru.java.mentor.oldranger.club.model.comment.Comment;
import ru.java.mentor.oldranger.club.model.forum.Subsection;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.forum.TopicVisitAndSubscription;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.projection.IdAndNumberProjection;
import ru.java.mentor.oldranger.club.service.forum.CommentService;
import ru.java.mentor.oldranger.club.service.forum.TopicService;
import ru.java.mentor.oldranger.club.service.forum.TopicVisitAndSubscriptionService;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@CacheConfig(cacheNames = {"topic"}, cacheManager = "generalCacheManager")
public class TopicServiceImpl implements TopicService {

    private TopicRepository topicRepository;
    private UserStatisticService userStatisticService;
    private SecurityUtilsService securityUtilsService;
    private TopicVisitAndSubscriptionService topicVisitAndSubscriptionService;
    private PhotoAlbumService albumService;
    private PhotoService photoService;
    private CommentService commentService;

    @Autowired
    @Lazy
    public TopicServiceImpl(TopicRepository topicRepository, UserStatisticService userStatisticService, SecurityUtilsService securityUtilsService, TopicVisitAndSubscriptionService topicVisitAndSubscriptionService, PhotoAlbumService albumService, PhotoService photoService, CommentService commentService) {
        this.topicRepository = topicRepository;
        this.userStatisticService = userStatisticService;
        this.securityUtilsService = securityUtilsService;
        this.topicVisitAndSubscriptionService = topicVisitAndSubscriptionService;
        this.albumService = albumService;
        this.photoService = photoService;
        this.commentService = commentService;
    }

    @Override
    public Page<Topic> getAllTopic(Pageable pageable) {
        log.debug("Getting page {} of topics", pageable.getPageNumber());
        Page<Topic> page = null;
        try {
            page = topicRepository.findAll(pageable);
            log.debug("Page returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return page;
    }

    @Override
    @CachePut(key = "#topic.id", condition = "#topic.id!=null")
    public Topic createTopic(Topic topic) {
        log.info("Saving topic {}", topic);
        Topic savedTopic = null;
        try {
            UserStatistic userStatistic = userStatisticService.getUserStaticByUser(topic.getTopicStarter());
            long topicCount = userStatistic.getTopicStartCount();
            userStatistic.setTopicStartCount(++topicCount);
            userStatisticService.saveUserStatic(userStatistic);
            savedTopic = topicRepository.save(topic);
            log.info("Topic saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return savedTopic;
    }

    @Override
    @CachePut(key = "#topic.id")
    public Topic editTopicByName(Topic topic) {
        log.info("Saving topic {}", topic);
        Topic savedTopic = null;
        try {
            savedTopic = topicRepository.save(topic);
            log.info("Topic saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return savedTopic;
    }

    @Override
    @CacheEvict
    public void deleteTopicById(Long id) {
        log.info("Deleting topic with id = {}", id);
        try {
            PhotoAlbum photoAlbum = albumService.findPhotoAlbumByTopic(topicRepository.findById(id).get());
            List<Photo> photos = albumService.getAllPhotosByAlbum(photoAlbum);
            if (photos != null) {
                for (Photo photo : photos) {
                    photoService.deletePhoto(photo.getId());
                }
            }
            List<Comment> commentsList = commentService.getAllCommentsByTopicId(id);
            if (commentsList != null) {
                for (Comment comment : commentsList) {
                    commentService.deleteComment(comment.getId());
                }
            }
            albumService.deleteAlbum(photoAlbum.getId());
            topicRepository.deleteById(id);
            log.info("Topic deleted");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    @Cacheable
    public Topic findById(Long id) {
        log.debug("Getting topic by id = {}", id);
        return topicRepository.findById(id).orElse(null);
    }

    @Override
    public List<Topic> findAll() {
        System.out.println("findAll");

        log.debug("Getting all topics");
        List<Topic> topics = null;
        try {
            topics = topicRepository.findAll();
            log.debug("Returned list of {} topics", topics.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return topics;
    }

    @Override
    public List<Topic> getActualTopicsLimitAnyBySection(Integer limitTopicsBySection) {
        log.debug("Getting actual topics with limit = {}", limitTopicsBySection);
        List<Topic> topics = null;
        try {
            topics = topicRepository.getActualTopicsLimitAnyBySection(limitTopicsBySection);
            log.debug("Returned list of {} topics", topics.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return topics;
    }

    @Override
    public List<Topic> getActualTopicsLimitAnyBySectionForAnon(int expecting_topics_limit_less_or_equals) {
        log.debug("Getting actual topics for anon with limit = {}", expecting_topics_limit_less_or_equals);
        List<Topic> topics = null;
        try {
            topics = topicRepository.getActualTopicsLimitAnyBySectionForAnon(expecting_topics_limit_less_or_equals);
            log.debug("Returned list of {} topics", topics.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return topics;
    }

    @Override
    public List<Topic> getActualTopicsLimit10() {
        if (securityUtilsService.isLoggedUserIsUser()) {
            return topicRepository.getActualTopicsLimit(10);
        } else {
            return topicRepository.getActualTopicsLimitForAnon(10);
        }
    }

    @Override
    public List<Topic> getActualTopicsLimit10BySection() {
        log.debug("Getting actual topics with limit = 10");
        System.out.println("get 10");
        List<Topic> topics = null;
        try {
            topics = topicRepository.getActualTopicsLimitAnyBySection(10);
            log.debug("Returned list of {} topics", topics.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return topics;
    }

    @Override
    public List<Topic> getActualTopicsLimit10BySectionForAnon() {
        log.debug("Getting actual topics for anon with limit = 10");
        List<Topic> topics = null;
        try {
            topics = topicRepository.getActualTopicsLimitAnyBySectionForAnon(10);
            log.debug("Returned list of {} topics", topics.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return topics;
    }

    @Override
    public Page<Topic> getPageableBySubsection(Subsection subsection, Pageable pageable) {
        log.debug("Getting page {} of topics for subsection with id = {}", pageable.getPageNumber(), subsection.getId());
        Page<Topic> page = null;
        try {
            if (securityUtilsService.isLoggedUserIsUser()) {
                page = getPageableBySubsectionForUser(securityUtilsService.getLoggedUser(), subsection, pageable);
            } else {
                page = getPageableBySubsectionForAnon(subsection, pageable);
            }
            log.debug("Page returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return page;
    }

    @Override
    public Page<Topic> getPageableBySubsectionWithFixTime(Subsection subsection, String dateTime, Pageable pageable) {
        Page<Topic> page;
        if (securityUtilsService.isLoggedUserIsUser()) {
            page = getPageableBySubsectionForUserWithFixTime(subsection, dateTime, pageable);
        } else {
            page = getPageableBySubsectionForAnonWithFixTime(subsection, dateTime, pageable);
        }

        return page;
    }

    public Page<Topic> getPageableBySubsectionForAnon(Subsection subsection, Pageable pageable) {
        log.debug("Getting page {} of topics for subsection for anon with id = {}", pageable.getPageNumber(), subsection.getId());
        Page<Topic> page = null;
        try {
            page = topicRepository.findBySubsectionAndIsHideToAnonIsFalseOrderByLastMessageTimeDesc(subsection, pageable);
            log.debug("Page returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return page;
    }

    public Page<Topic> getPageableBySubsectionForAnonWithFixTime(Subsection subsection, String dateTime, Pageable pageable) {
        return topicRepository.findBySubsectionWithTimeForAnon(subsection, dateTime, pageable);
    }

    public Page<Topic> getPageableBySubsectionForUserWithFixTime(Subsection subsection, String dateTime, Pageable pageable) {
        return topicRepository.findBySubsectionWithTimeForUser(subsection, dateTime, pageable);
    }

    @Override
    public Page<Topic> findAllTopicsStartedByUser(User user, Pageable pageable) {
        log.debug("Getting page {} of topics started by user with id = {}", pageable.getPageNumber(), user.getId());
        Page<Topic> page = null;
        try {
            page = topicRepository.findAllBytopicStarter(user, pageable);
            log.debug("Page returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return page;
    }

    @Override
    public Page<Topic> getPageableBySubsectionForUser(User user, Subsection subsection, Pageable pageable) {
        log.debug("Getting page {} of topics by subsection id = {} and user id = {}", pageable.getPageNumber(), subsection.getId(), user.getId());
        PageImpl<Topic> page = null;
        try {
            int pageNumber = pageable.getPageNumber();
            int pageSize = pageable.getPageSize();
            int offset = pageNumber * pageSize;
            page = new PageImpl<>(
                    topicRepository.getSliceListBySubsectionForUserOrderByLastMessageTimeDescAndSubscriptionsWithNewMessagesFirst(user.getId(), subsection.getId(), offset, pageSize),
                    pageable,
                    topicRepository.countForGetSliceListBySubsectionForUserOrderByLastMessageTimeDescAndSubscriptionsWithNewMessagesFirst(user.getId(), subsection.getId())
            );
            log.debug("Page returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return page;
    }

    @Override
    public List<IdAndNumberProjection> getMessagesCountForTopics(List<Topic> topics) {
        log.debug("Getting messages count for topics");
        List<IdAndNumberProjection> projections = null;
        try {
            List<Long> list = topics.stream().map(Topic::getId).collect(Collectors.toList());
            projections = topicRepository.getPairsTopicIdAndTotalMessagesCount(list);
            log.debug("Returned list of {} projections", projections.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return projections;
    }

    @Override
    public List<IdAndNumberProjection> getNewMessagesCountForTopicsAndUser(List<Topic> topics, User user) {
        log.debug("Getting new messages count for user with id = {}", user.getId());
        List<IdAndNumberProjection> projections = null;
        try {
            List<Long> list = topics.stream().map(Topic::getId).collect(Collectors.toList());
            projections = topicRepository.getPairsTopicIdAndNewMessagesCountForUserId(list, user.getId());
            log.debug("Returned list of {} projections", projections.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return projections;
    }

    @Override
    public List<TopicAndNewMessagesCountDto> getTopicsDto(List<Topic> topics) {
        log.debug("Getting list of topic dtos");
        boolean logged = false;
        List<IdAndNumberProjection> newMessagesCountForTopicsAndUser = null;
        List<TopicVisitAndSubscription> topicVisitAndSubscriptionForUser = null;
        List<TopicAndNewMessagesCountDto> dtos = new ArrayList<>();
        try {
            if (securityUtilsService.isLoggedUserIsUser()) {
                logged = true;
                User loggedUser = securityUtilsService.getLoggedUser();
                newMessagesCountForTopicsAndUser = getNewMessagesCountForTopicsAndUser(topics, loggedUser);
                topicVisitAndSubscriptionForUser = topicVisitAndSubscriptionService.getTopicVisitAndSubscriptionForUser(loggedUser);
            }
            List<IdAndNumberProjection> messagesCountForTopics = getMessagesCountForTopics(topics);
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
            log.debug("Returned list of {} dtos", dtos.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return dtos;
    }

}
