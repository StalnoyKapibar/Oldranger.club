package ru.java.mentor.oldranger.club.service.utils.impl;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.SearchRepository.SearchRepository;
import ru.java.mentor.oldranger.club.model.article.Article;
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
        if (comments.size() == 0) {
            return comments;
        } else {
            return pageable(comments, page, limit);
        }
    }

    public List searchAllCommentByText(String queryString) {
        log.debug("Searching in comments {}", queryString);
        String[] targetFields = {"commentText"};
        List comments = searchRepository.searchObjectsByName(queryString, null, targetFields, Comment.class);
       return comments;
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

        if (page == null) {
            page = 0;
        }
        if (limit == null || limit == 0) {
            limit = 10;
        }
        Object[] objects;

        int count = list.size();
        int countLastPage = count % limit == 0 ? limit : count % limit;
        int pages;
        if (countLastPage == 10) {
            pages = count / limit;
        } else {
            pages = count / limit == 0 ? 1 : count / limit + 1;
        }

        int startIndex = page == 0 ? 0 : page * limit;

        if (page + 1 == pages) {
            objects = new Object[countLastPage];
            System.arraycopy(list.toArray(), startIndex, objects, 0, countLastPage);
        } else {
            objects = new Object[limit];
            System.arraycopy(list.toArray(), startIndex, objects, 0, limit);
        }
        return Arrays.stream(objects).collect(Collectors.toList());
    }

    public List searchByArticleNameLimitPage(String queryString, Integer page, Integer limit) {
        log.debug("Searching by article name {}", queryString);
        String[] targetFields = {"title"};
        List list = searchRepository.searchObjectsByName(queryString, null, targetFields, Article.class);
        return pageable(list, page, limit);
    }

    public List searchByArticleName(String queryString) {
        log.debug("Searching by article name {}", queryString);
        String[] targetFields = {"title"};
        return searchRepository.searchObjectsByName(queryString, null, targetFields, Article.class);
    }
}
