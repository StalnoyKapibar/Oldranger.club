package ru.java.mentor.oldranger.club.service.article.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleCommentRepository;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleRepository;
import ru.java.mentor.oldranger.club.dto.ArticleAndCommentsDto;
import ru.java.mentor.oldranger.club.dto.ArticleCommentDto;
import ru.java.mentor.oldranger.club.dto.ArticleListAndCountArticlesDto;
import ru.java.mentor.oldranger.club.dto.ArticlePhotosDTO;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleTag;
import ru.java.mentor.oldranger.club.model.comment.ArticleComment;
import ru.java.mentor.oldranger.club.model.comment.Comment;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;
import ru.java.mentor.oldranger.club.service.article.ArticleService;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.user.UserStatisticService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@AllArgsConstructor
@CacheConfig(cacheNames = {"article"})
public class ArticleServiceImpl implements ArticleService {

    private ArticleRepository articleRepository;
    private ArticleCommentRepository articleCommentRepository;
    private UserStatisticService userStatisticService;
    private  PhotoService photoService;
    @Override
    public Page<Article> getAllArticles(Pageable pageable) {
        return articleRepository.findAllByDraftIsFalse(pageable);
    }

    @Override
    public Page<Article> getAllByTag(Set<ArticleTag> tagId, Pageable pageable) {
        return articleRepository.findDistinctByDraftIsFalseAndArticleTagsIn(tagId, pageable);
    }

    @Override
    public Page<Article> getAllByTitle(String title, Pageable pageable) {
        return articleRepository.findAllByTitle(title, pageable);
    }

    @Override
    public Page<Article> getArticleDraftByUser(User user, Pageable pageable) {
        return articleRepository.findAllByDraftIsTrueAndUser(user, pageable);
    }

    @Override
    @Cacheable
    public Article getArticleById(Long id) {
        log.debug("Getting article by id = {}", id);
        return articleRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Article> getArticlesForAnon(Pageable pageable) {
        return articleRepository.getArticlesForAnon(pageable);
    }

    @Override
    @CachePut(key = "#article.id", condition = "#article.id!=null")
    public Article addArticle(Article article) {
        return articleRepository.save(article);
    }

    @Override
    @CacheEvict(key = "#articleComment.article.id")
    public void addCommentToArticle(ArticleComment articleComment) {
        Article article = articleComment.getArticle();
        long comments = article.getCommentCount();
        articleComment.setPosition(++comments);
        article.setCommentCount(comments);
        articleCommentRepository.save(articleComment);
        UserStatistic userStatistic = userStatisticService.getUserStaticByUser(articleComment.getUser());
        userStatistic.setLastComment(articleComment.getDateTime());
        userStatisticService.saveUserStatic(userStatistic);
    }

    @Override
    public ArticleCommentDto assembleCommentToDto(ArticleComment articleComment) {
        ArticleCommentDto articleCommentDto = new ArticleCommentDto();

        LocalDateTime replyTime = null;
        String replyNick = null;
        String replyText = null;
        Long parentId = -1L;
        if (articleComment.getAnswerTo() != null) {
            replyTime = articleComment.getAnswerTo().getDateTime();
            replyNick = articleComment.getAnswerTo().getUser().getNickName();
            replyText = articleComment.getAnswerTo().getCommentText();
            parentId = articleComment.getAnswerTo().getId();
        }
        articleCommentDto.setPhotos(photoService.findByAlbumTitleAndDescription("PhotoAlbum by " +
                articleComment.getArticle().getTitle(), articleComment.getId().toString()));
        articleCommentDto = new ArticleCommentDto(
                articleComment.getPosition(),
                articleComment.getId(),
                articleComment.getArticle().getId(),
                articleComment.getUser(),
                articleComment.getDateTime(),
                replyTime, parentId, replyNick, replyText,
                articleComment.getCommentText(),
                articleComment.isDeleted(),
                articleCommentDto.getPhotos());
        return articleCommentDto;
    }

    @Override
    public ArticleListAndCountArticlesDto assembleArticleListAndCountArticleDto(List<Article> articles, long countArticles) {
        ArticleListAndCountArticlesDto articleListDto = new ArticleListAndCountArticlesDto(articles, countArticles);
        return articleListDto;
    }


    @Override
    @Cacheable(value = "articleComment")
    public ArticleComment getCommentById(Long id) {
        Optional<ArticleComment> comment = articleCommentRepository.findById(id);
        return comment.orElseThrow(() -> new RuntimeException("Not found comment by id: " + id));
    }

    @Override
    @CachePut(value = "articleComment", key = "#articleComment.id")
    public ArticleComment updateArticleComment(ArticleComment articleComment) {
        return articleCommentRepository.save(articleComment);
    }

    @Override
    @CacheEvict(value = "articleComment")
    public void deleteComment(Long id) {
        articleCommentRepository.deleteById(id);
    }

    @Override
    public List<ArticleCommentDto> getAllByArticle(Article article) {
        log.debug("Getting list of comment dtos for article with id = {}", article.getId());
        List<ArticleCommentDto> articleCommentDto = null;
        List<ArticleComment> articleComments = new ArrayList<>();
        try {
            articleComments = articleCommentRepository.findByArticle(article);
            for (ArticleComment comment : articleComments) {
                if (comment.isDeleted()
                        && comment.getCommentText().equals("Комментарий был удален")
                        && getChildComment(comment).isEmpty()) {
                    deleteComment(comment.getId());
                }
            }
            List<ArticleCommentDto> list;
            if (articleComments.size() != 0) {
                list = articleComments.subList(0, articleComments.size()).
                        stream().map(this::assembleCommentToDto).collect(Collectors.toList());
            } else {
                list = Collections.emptyList();
            }
            articleCommentDto = list;
            log.debug("Page returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return articleCommentDto;
    }

    @Override
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public void deleteArticles(List<Long> ids) {
        articleRepository.deleteAllByIdIn(ids);
    }

    @Override
    public List<ArticleComment> getChildComment(ArticleComment comment) {
        log.debug("Getting list childComment with idAnswerTo = {}", comment.getId());
        List<ArticleComment> childComment = articleCommentRepository.findAllByAnswerTo(comment);
        return childComment;
    }
}