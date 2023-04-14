package ru.oldranger.club.service.article;

import ru.oldranger.club.model.user.User;

import java.util.Set;

public interface ArticleLikeService {

    long countLikes(long articleId);

    Set<User> isUserLiked(long articleId, long userId);

    void saveLike(long articleId, long userId);

    void deleteLike(long articleId, long userId);
}
