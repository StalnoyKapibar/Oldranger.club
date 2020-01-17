package ru.java.mentor.oldranger.club.service.article.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleCommentRepository;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleLikeRepositoryEntityManager;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleRepository;
import ru.java.mentor.oldranger.club.dto.ArticleCommentDto;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleComment;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.article.ArticleService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private ArticleRepository articleRepository;
    private ArticleCommentRepository articleCommentRepository;
    private UserStatisticService userStatisticService;
    private ArticleLikeRepositoryEntityManager articleLikeRepositoryEntityManager;

    @Override
    public List<Article> getAllArticles() {
        return articleRepository.findAll(Sort.by("date").descending());
    }

    @Override
    public Page<Article> getAllByTag(Set<ArticleTag> tagId, Pageable pageable) {
        return articleRepository.findDistinctByArticleTagsIn(tagId, pageable);
    }

    @Override
    public Article getArticleById(long id) {
        return articleRepository.findById(id);
    }

    @Override
    public Page<Article> getArticlesForAnon(Pageable pageable) {
        return articleRepository.getArticlesForAnon(pageable);
    }

    @Override
    public void addArticle(Article article) {
        articleRepository.save(article);
    }

    @Override
    public void addCommentToArticle(ArticleComment articleComment) {
        Article article = articleComment.getArticle();
        long comments = article.getCommentCount();
        articleComment.setPositionInArticle(++comments);
        article.setCommentCount(comments);
        articleCommentRepository.save(articleComment);
        UserStatistic userStatistic = userStatisticService.getUserStaticByUser(articleComment.getUser());
        comments = userStatistic.getMessageCount();
        userStatistic.setMessageCount(++comments);
        userStatistic.setLastComment(articleComment.getDateTime());
        userStatisticService.saveUserStatic(userStatistic);
    }

    @Override
    public ArticleCommentDto conversionCommentToDto(ArticleComment articleComment) {
        ArticleCommentDto articleCommentDto;

        LocalDateTime replyTime = null;
        String replyNick = null;
        String replyText = null;
        if (articleComment.getAnswerTo() != null) {
            replyTime = articleComment.getAnswerTo().getDateTime();
            replyNick = articleComment.getAnswerTo().getUser().getNickName();
            replyText = articleComment.getAnswerTo().getCommentText();
        }

        articleCommentDto = new ArticleCommentDto(
                articleComment.getPositionInArticle(),
                articleComment.getArticle().getId(),
                articleComment.getUser(),
                articleComment.getDateTime(),
                userStatisticService.getUserStaticById(articleComment.getUser().getId()).getMessageCount(),
                replyTime, replyNick, replyText,
                articleComment.getCommentText());

        return articleCommentDto;
    }

    @Override
    public ArticleComment getCommentById(Long id) {
        Optional<ArticleComment> comment = articleCommentRepository.findById(id);
        return comment.orElseThrow(() -> new RuntimeException("Not found comment by id: " + id));
    }

    @Override
    public void updateArticleComment(ArticleComment articleComment) {
        articleCommentRepository.save(articleComment);
    }

    @Override
    public void deleteComment(Long id) {
        articleCommentRepository.deleteById(id);
    }

    @Override
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteArticles(List<Long> ids) {
        articleRepository.deleteAllByIdIn(ids);
    }

    @Override
    public Set isUserLikedtest(long articleId, long userId) {
        return articleRepository.isUserLikedtest(articleId, userId);
    }


    @Override
    public void addUser(long articleId, long userId) {
        articleLikeRepositoryEntityManager.addUser(articleId, userId);
    }

    @Override
    public void removeUser(long articleId, long userId) {
        articleLikeRepositoryEntityManager.removeUser(articleId, userId);
    }

    @Override
    public long countLikes(long articleId) {
        return articleRepository.countLikes(articleId);
    }

}
