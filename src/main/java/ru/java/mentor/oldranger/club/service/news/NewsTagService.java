package ru.java.mentor.oldranger.club.service.news;

import ru.java.mentor.oldranger.club.model.news.NewsTag;

import java.util.List;

public interface NewsTagService {

    List<NewsTag> getAllTags();

    NewsTag getTagById(Long id);

    void addTag(NewsTag newsTag);
}
