package ru.java.mentor.oldranger.club.service.utils;

import java.util.List;

public interface SearchService {

    List searchByTopicName(String queryString);

    List searchByComment(String queryString, Integer page, Integer limit);

    List searchTopicsByNode(String finderTag, Integer node, Long nodeValue);
}
