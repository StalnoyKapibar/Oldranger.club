package ru.java.mentor.oldranger.club.dao.ArticleRepository;

import org.hibernate.annotations.Parameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.user.User;

import java.util.List;
import java.util.Set;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findAllByArticleTags_id(long id);

    Article findById(long id);


    void deleteAllByIdIn(List<Long> ids);

    @Query(nativeQuery = true,
            value = "select * from articles a where a.article_hide = false")
    Page<Article> getArticlesForAnon(Pageable pageable);

    @Query(nativeQuery = true, value = "select * from like_users where article_id = ?1 and user_id = ?2")
    Set isUserLikedtest(long articleId, long userId);

    //@Modifying
    //@Query("SELECT COUNT(a.likes) FROM Article a WHERE a.id = :articleId")
    @Query(nativeQuery = true, value = "SELECT COUNT(article_id) FROM like_users WHERE article_id = :articleId")
    long countLikes(long articleId);

//    @Modifying
//    @Query(nativeQuery = true, value = "insert into like_users(article_id, user_id) values(:aricleId, :userId)")
//    void addUser( Long articleId, Long userId);
//
//    @Modifying
//    @Query(nativeQuery = true, value = "delete from like_users where article_id = :article_id and user_id = :user_id")
//    void removeUser( Long articleId,Long userId);

    //@Query(value = "SELECT CASE WHEN :user MEMBER OF a.likes THEN true ELSE false END FROM Article a WHERE a.id = :articleId")
    //@Query(value = "SELECT CASE WHEN :user MEMBER OF a.likes THEN 1 FROM Article a WHERE a.id = :articleId WHEN false END")
    //SELECT :user, CASE WHEN MEMBER OF a.likes THEN 1 WHEN (e.salary < 100000) THEN 2 ELSE 0 END FROM Employee e
//    @Query(value = "SELECT a FROM Article a where a.id = :articleId :user MEMBER OF a.likes")
//    User isUserLiked(@Param("articleId") long articleId, @Param("user") User user);
}