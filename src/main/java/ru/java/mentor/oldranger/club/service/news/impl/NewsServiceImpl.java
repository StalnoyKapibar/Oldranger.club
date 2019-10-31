package ru.java.mentor.oldranger.club.service.news.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.NewsRepository;
import ru.java.mentor.oldranger.club.model.News;
import ru.java.mentor.oldranger.club.service.news.NewsService;

import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsRepository newsRepository;

    @Override
    public List<News> getAllNews() {
        return newsRepository.findAll();
    }
}
