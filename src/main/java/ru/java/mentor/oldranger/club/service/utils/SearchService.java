package ru.java.mentor.oldranger.club.service.utils;

import ru.java.mentor.oldranger.club.model.forum.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;

import java.util.List;

public interface SearchService {

    List<Topic> searchByTopicName(String queryString);

    List<Comment> searchByComment(String queryString);
}
