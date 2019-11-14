package ru.java.mentor.oldranger.club.service.news.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.newsRepository.NewsRepository;
import ru.java.mentor.oldranger.club.model.news.News;
import ru.java.mentor.oldranger.club.model.news.NewsTag;
import ru.java.mentor.oldranger.club.service.news.NewsService;

import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    private NewsRepository newsRepository;

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Override
    public List<News> getAllNews() {
        return newsRepository.findAll(Sort.by("date").descending());
    }

    @Override
    public List<News> getAllByTag(NewsTag newsTag) {
        return newsRepository.getAllByNewsTagOrderByDateDesc(newsTag);
    }

    @Override
    public void addNews(News news) {
        newsRepository.save(news);
    }
}