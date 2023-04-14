package ru.oldranger.club.service.utils;

import java.util.List;

public interface SearchService {

    List searchByTopicName(String queryString);

    List searchByComment(String queryString, Integer page, Integer limit);

    List searchTopicsByPageAndLimits(String finderTag, Integer page, Integer limit, Integer node, Long nodeValue);
}
