package ru.java.mentor.oldranger.club.dao.ArticleRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;

public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long> {

}
