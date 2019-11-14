package ru.java.mentor.oldranger.club.service.news.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.newsRepository.NewsTagRepository;
import ru.java.mentor.oldranger.club.model.news.NewsTag;
import ru.java.mentor.oldranger.club.service.news.NewsTagService;

import java.util.List;

@Service
public class NewsTagServiceImpl implements NewsTagService {

    private NewsTagRepository newsTagRepository;

    @Autowired
    public NewsTagServiceImpl(NewsTagRepository newsTagRepository) {
        this.newsTagRepository = newsTagRepository;
    }

    @Override
    public List<NewsTag> getAllTags() {
        return newsTagRepository.findAll();
    }

    @Override
    public NewsTag getTagById(Long id) {
        return newsTagRepository.findById(id).orElse(null);
    }

    @Override
    public void addTag(NewsTag newsTag) {
        newsTagRepository.save(newsTag);
    }
}
