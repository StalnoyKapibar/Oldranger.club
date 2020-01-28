package ru.java.mentor.oldranger.club.dao.ArticleRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.user.User;

import java.util.Set;

public interface ArticleCommentLikeRepository extends JpaRepository<Article, Long> {

    @Query(nativeQuery = true, value = "select * from like_users where article_id = ?1 and user_id = ?2")
    Set<User> isUserLiked(long articleId, long userId);

    @Query(nativeQuery = true, value = "SELECT COUNT(article_id) FROM like_users WHERE article_id = :articleId")
    long countLikes(long articleId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "insert into like_users(article_id, user_id) values(?1, ?2)")
    void saveLike(long articleId, long userId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "delete from like_users where ?1 and ?2")
    void deleteLike(long articleId, long userId);
}
