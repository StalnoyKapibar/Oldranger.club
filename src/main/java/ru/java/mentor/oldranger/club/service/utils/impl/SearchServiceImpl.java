package ru.java.mentor.oldranger.club.service.utils.impl;


import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.SearchRepository.SearchRepository;
import ru.java.mentor.oldranger.club.model.forum.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.service.utils.SearchService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
    private SearchRepository searchRepository;

    public SearchServiceImpl(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @Deprecated
    public List searchByTopicName(String queryString) {
        String[] targetFields = {"name"};
        return searchRepository.searchObjectsByName(queryString, null, targetFields, Topic.class);
    }

    public List searchByComment(String queryString) {
        String[] targetFields = {"commentText"};
        return searchRepository.searchObjectsByName(queryString, null, targetFields, Comment.class);
    }

    @Override
    public List searchTopicsByNode(String finderTag, Integer node, Long nodeValue) {
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
