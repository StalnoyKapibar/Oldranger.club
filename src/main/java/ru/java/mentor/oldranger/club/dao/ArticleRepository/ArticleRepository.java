package ru.java.mentor.oldranger.club.dao.ArticleRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.article.ArticlesSection;

import java.util.List;
import java.util.Set;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findDistinctByArticleTagsIn(Set<ArticleTag> tags, Pageable pageable);

    Page<Article> findAllByArticlesSections(ArticlesSection articlesSections, Pageable pageable);

    Page<Article> findDistinctByArticlesSectionsIn(Set<ArticlesSection> articlesSections, Pageable pageable);

    Article findById(long id);

    void deleteAllByIdIn(List<Long> ids);
  
    @Query(nativeQuery = true,
            value = "select * from articles a where a.article_hide = false")
    Page<Article> getArticlesForAnon(Pageable pageable);
}