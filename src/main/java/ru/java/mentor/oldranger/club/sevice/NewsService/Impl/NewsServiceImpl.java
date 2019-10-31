package ru.java.mentor.oldranger.club.sevice.NewsService.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.NewsRepository;
import ru.java.mentor.oldranger.club.model.News;
import ru.java.mentor.oldranger.club.sevice.NewsService.NewsService;

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
