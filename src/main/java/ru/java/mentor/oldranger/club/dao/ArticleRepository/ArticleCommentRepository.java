package ru.java.mentor.oldranger.club.dao.ArticleRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleComment;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {

    Page<ArticleComment> findByArticle(Article article, Pageable pageable);
}
