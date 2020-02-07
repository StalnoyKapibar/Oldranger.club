package ru.java.mentor.oldranger.club.dao.ArticleRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.user.User;

import java.util.List;
import java.util.Set;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    //ToDo N+1 problem analysis  - потенциально опасный метод
    Page<Article> findDistinctByDraftIsFalseAndArticleTagsIn(Set<ArticleTag> tags, Pageable pageable);

    //ToDo N+1 problem analysis  - потенциально опасный метод
    Page<Article> findAllByDraftIsFalse(Pageable pageable);

    //ToDo N+1 problem analysis  - потенциально опасный метод
    Page<Article> findAllByDraftIsTrueAndUser(User user, Pageable pageable);

    Article findById(long id);

    void deleteAllByIdIn(List<Long> ids);

    //ToDo N+1 problem analysis  - потенциально опасный метод
    @Query(nativeQuery = true,
            value = "select * from articles a where a.article_hide = false")
    Page<Article> getArticlesForAnon(Pageable pageable);

}