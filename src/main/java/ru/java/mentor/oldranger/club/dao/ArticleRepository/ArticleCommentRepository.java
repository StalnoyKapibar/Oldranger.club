package ru.java.mentor.oldranger.club.dao.ArticleRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.dto.ArticleCommentDto;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.comment.ArticleComment;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {

    @Query(value = "select new ru.java.mentor.oldranger.club.dto.ArticleCommentDto (ac.position, ac.id, ac.user, ac.dateTime, a.commentCount, rc.dateTime, rcu.nickName, ac.commentText, rc.commentText) \n" +
            " from ArticleComment ac join ac.article a join ac.answerTo rc join rc.user rcu where a=:article")
    Page<ArticleCommentDto> findByArticle(Article article, Pageable pageable);
}
