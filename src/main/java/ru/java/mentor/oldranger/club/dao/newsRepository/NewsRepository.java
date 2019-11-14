package ru.java.mentor.oldranger.club.dao.newsRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.news.News;
import ru.java.mentor.oldranger.club.model.news.NewsTag;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> getAllByNewsTagOrderByDateDesc(NewsTag newsTag);
}