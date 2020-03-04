package ru.java.mentor.oldranger.club.dao.ArticleRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;

import java.util.List;
import java.util.Set;

public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long> {
    Set<ArticleTag> findByIdIn(List<Long> id);

    ArticleTag findArticleTagByName(String name);
}
