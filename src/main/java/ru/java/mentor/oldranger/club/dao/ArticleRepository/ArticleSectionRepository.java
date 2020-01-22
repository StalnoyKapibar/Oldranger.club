package ru.java.mentor.oldranger.club.dao.ArticleRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.article.ArticlesSection;

import java.util.List;
import java.util.Set;

public interface ArticleSectionRepository extends JpaRepository<ArticlesSection, Long> {
    Set<ArticlesSection> findByIdIn(List<Long> id);
}
