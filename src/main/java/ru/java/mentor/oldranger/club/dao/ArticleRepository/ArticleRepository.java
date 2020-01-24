package ru.java.mentor.oldranger.club.dao.ArticleRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;

import java.util.List;
import java.util.Set;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findDistinctByArticleTagsIn(Set<ArticleTag> tags, Pageable pageable);


    Article findById(long id);

    void deleteAllByIdIn(List<Long> ids);

    @Query(nativeQuery = true,
            value = "select * from articles a where a.article_hide = false")
    Page<Article> getArticlesForAnon(Pageable pageable);

    @Query(nativeQuery = true, value = "select * from like_users where article_id = ?1 and user_id = ?2")
    Set isUserLiked(long articleId, long userId);


    @Query(nativeQuery = true, value = "SELECT COUNT(article_id) FROM like_users WHERE article_id = :articleId")
    long countLikes(long articleId);


    @Modifying
    @Query(nativeQuery = true,
            value = "insert into like_users(article_id, user_id) values(?1, ?2)")
    void saveLike(long articleId, long userId);

    @Modifying
    @Query(nativeQuery = true,
            value = "delete from like_users where ?1 and ?2")
    void deleteLike(long articleId, long userId);

}