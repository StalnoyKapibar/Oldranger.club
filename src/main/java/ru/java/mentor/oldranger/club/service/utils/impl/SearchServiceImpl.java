package ru.java.mentor.oldranger.club.service.utils.impl;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.SearchRepository.SearchRepository;
import ru.java.mentor.oldranger.club.model.comment.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.service.utils.SearchService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {
    private SearchRepository searchRepository;

    @Deprecated
    public List searchByTopicName(String queryString) {
        log.debug("Searching by topic name {}", queryString);
        String[] targetFields = {"name"};
        return searchRepository.searchObjectsByName(queryString, null, targetFields, Topic.class);
    }

    public List searchByComment(String queryString, Integer page, Integer limit) {
        log.debug("Searching in comments {}", queryString);
        String[] targetFields = {"commentText"};
        /*
         * В будущем если к CommentDto потребуются поля из Comment расскоментировать нижнию строку.
         * String[] fetchingField = {"topic", "user", "answerTo"}; // Список полей которые нужно получить, вместо null.
         * А также добавить в параметр fetchingField, вместо null
         */
        List comments = searchRepository.searchObjectsByName(queryString, null, targetFields, Comment.class);
        return pageable(comments, page, limit);
    }


    public List searchTopicsByNode(String finderTag, Integer node, Long nodeValue) {
        log.debug("Searching topics by node {}, nodeValue = {}, finderTag = {}", node, nodeValue, finderTag);
        String[] fetchingFields = {"subsection", "subsection.section"};
        String[] targetFields = {"name"};
        if (node == null) node = 0;
        if (nodeValue == null) nodeValue = 0L;
        List<Topic> topics = searchRepository.searchObjectsByName(finderTag, fetchingFields, targetFields, Topic.class);
        switch (node) {
            case 0: {
                return topics;
            }
            case 1: {
                Long finalNodeValue = nodeValue;
                List<Topic> topicsList = topics
                        .stream()
                        .filter(topic -> topic.getSection().getId().equals(finalNodeValue))
                        .collect(Collectors.toList());
                return topicsList;
            }
            case 2: {
                Long finalNodeValue1 = nodeValue;
                return topics
                        .stream()
                        .filter(topic -> topic.getSubsection().getId().equals(finalNodeValue1))
                        .collect(Collectors.toList());
            }
            default:
                return null;
        }
    }

    @Override
    public List searchTopicsByPageAndLimits(String finderTag, Integer page, Integer limit, Integer node, Long nodeValue) {
        List topics = searchTopicsByNode(finderTag, node, nodeValue);
        return pageable(topics, page, limit);
    }

    private List pageable(List list, Integer page, Integer limit) {
        if (list == null) {
            return null;
        }

        if (page == null || page == 0) {
            page = 0;
        }
        if (limit == null || limit == 0) {
            limit = 10;
        }
        Object[] objects;

        int count = list.size();
        int countLastPage = count % limit == 0 ? limit : count % limit;
        int pages = (count / limit) == 0 ? 1 : count / limit;

        int startIndex;

        if (page - 1 < 0) {
            startIndex = 0;
            objects = new Object[countLastPage];
            System.arraycopy(list.toArray(), startIndex, objects, 0, countLastPage);
            return Arrays.stream(objects).collect(Collectors.toList());
        } else {
            startIndex = pages > page - 1 ? page * limit - 1 : (pages * limit) - limit;
        }
        if (pages <= page) {
            objects = new Object[countLastPage];
            System.arraycopy(list.toArray(), startIndex, objects, 0, countLastPage);
            return Arrays.stream(objects).collect(Collectors.toList());
        } else if (pages > page) {
            objects = new Object[limit];
            System.arraycopy(list.toArray(), startIndex, objects, 0, limit);
            return Arrays.stream(objects).collect(Collectors.toList());
        }

        return null;
    }
}
