package ru.java.mentor.oldranger.club.service.article;

import java.util.Set;

public interface ArticleLikeService {

    long countLikes(long articleId);

    Set isUserLiked(long articleId, long userId);

    void saveLike(long articleId, long userId);

    void deleteLike(long articleId, long userId);
}
