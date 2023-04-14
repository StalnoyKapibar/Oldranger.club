package ru.oldranger.club.service.article.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.oldranger.club.dao.ArticleRepository.ArticleCommentLikeRepository;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.service.article.ArticleLikeService;

import java.util.Set;

@Service
@AllArgsConstructor
public class ArticleLikeServiceImpl implements ArticleLikeService {

    private ArticleCommentLikeRepository articleCommentLikeRepository;

    @Override
    public Set<User> isUserLiked(long articleId, long userId) {
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
