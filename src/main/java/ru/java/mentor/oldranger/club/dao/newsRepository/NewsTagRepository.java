package ru.java.mentor.oldranger.club.dao.newsRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.news.NewsTag;

public interface NewsTagRepository extends JpaRepository<NewsTag, Long> {
}
