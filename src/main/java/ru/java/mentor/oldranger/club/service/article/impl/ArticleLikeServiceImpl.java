package ru.java.mentor.oldranger.club.service.article.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleCommentLikeRepository;
import ru.java.mentor.oldranger.club.service.article.ArticleLikeService;

import java.util.Set;

@Service
@AllArgsConstructor
public class ArticleLikeServiceImpl implements ArticleLikeService {

    private ArticleCommentLikeRepository articleCommentLikeRepository;

    @Override
    public Set isUserLiked(long articleId, long userId) {
        return articleCommentLikeRepository.isUserLiked(articleId, userId);
    }

    @Override
    public long countLikes(long articleId) {
        return articleCommentLikeRepository.countLikes(articleId);
    }

    @Override
    public void saveLike(long articleId, long userId) {
        articleCommentLikeRepository.saveLike(articleId, userId);
    }

    @Override
    public void deleteLike(long articleId, long userId) {
        articleCommentLikeRepository.deleteLike(articleId, userId);
    }
}
