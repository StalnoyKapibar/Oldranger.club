package ru.java.mentor.oldranger.club.service.utils.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.SearchRepository.SearchRepository;
import ru.java.mentor.oldranger.club.model.forum.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.service.utils.SearchService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
    private SearchRepository searchRepository;
    private static final Logger LOG = LoggerFactory.getLogger(SearchServiceImpl.class);

    public SearchServiceImpl(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @Deprecated
    public List searchByTopicName(String queryString) {
        LOG.debug("Searching by topic name {}", queryString);
        String[] targetFields = {"name"};
        return searchRepository.searchObjectsByName(queryString, null, targetFields, Topic.class);
    }

    public List searchByComment(String queryString, Integer page, Integer limit) {
        LOG.debug("Searching in comments {}", queryString);
        String[] targetFields = {"commentText"};
        /*
         * В будущем если к CommentDto потребуются поля из Comment расскоментировать нижнию строку.
         * String[] fetchingField = {"topic", "user", "answerTo"}; // Список полей которые нужно получить, вместо null.
         * А также добавить в параметр fetchingField, вместо null
         */
        List<Comment> comments = searchRepository.searchObjectsByName(queryString, null, targetFields, Comment.class);

        if (comments == null) {
            return null;
        }

        if (page == null || page == 0) {
            page = 1;
        }
        if (limit == null || limit == 0) {
            limit = 10;
        }
        Comment[] commentArr;

        int countComments = comments.size();
        int countLastPageComments = countComments % limit == 0 ? limit : countComments % limit;
        int pages = (countComments / limit) == 0 ? 1 : countComments / limit;

        int startIndex = pages > page ? page * limit - 1 : (pages * limit) - limit;

        if (pages <= page) {
            commentArr = new Comment[countLastPageComments];
            System.arraycopy(comments.toArray(), startIndex, commentArr, 0, countLastPageComments);
            return Arrays.stream(commentArr).collect(Collectors.toList());
        } else if (pages > page) {
            commentArr = new Comment[limit];
            System.arraycopy(comments.toArray(), startIndex, commentArr, 0, limit);
            return Arrays.stream(commentArr).collect(Collectors.toList());
        }

        return null;
    }

    @Override
    public List searchTopicsByNode(String finderTag, Integer node, Long nodeValue) {
        LOG.debug("Searching topics by node {}, nodeValue = {}, finderTag = {}", node, nodeValue, finderTag);
        String[] fetchingFields = {"subsection", "subsection.section"};
        String[] targetFields = {"name"};
        List<Topic> topics = searchRepository.searchObjectsByName(finderTag, fetchingFields, targetFields, Topic.class);
        switch (node) {
            case 0: {
                return topics;
            }
            case 1: {
                List<Topic> topicsList = topics
                        .stream()
                        .filter(topic -> topic.getSection().getId() == nodeValue)
                        .collect(Collectors.toList());
                return topicsList;
            }
            case 2: {
                return topics
                        .stream()
                        .filter(topic -> topic.getSubsection().getId() == nodeValue)
                        .collect(Collectors.toList());
            }
            default:
                return null;
        }
    }

}
