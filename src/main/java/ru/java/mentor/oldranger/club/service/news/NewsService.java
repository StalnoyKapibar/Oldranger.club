package ru.java.mentor.oldranger.club.service.news;

import ru.java.mentor.oldranger.club.model.news.News;
import ru.java.mentor.oldranger.club.model.news.NewsTag;

import java.util.List;

public interface NewsService {

    List<News> getAllNews();

    List<News> getAllByTag(NewsTag newsTag);

    void addNews(News news);
}