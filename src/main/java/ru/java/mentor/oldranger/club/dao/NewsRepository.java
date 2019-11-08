package ru.java.mentor.oldranger.club.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.News;

public interface NewsRepository extends JpaRepository<News, Long> {
}