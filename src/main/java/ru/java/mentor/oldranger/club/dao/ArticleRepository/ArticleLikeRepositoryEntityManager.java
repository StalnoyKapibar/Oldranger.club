package ru.java.mentor.oldranger.club.dao.ArticleRepository;

public interface ArticleLikeRepositoryEntityManager {

    void addUser(long articleId, long userId);

    void removeUser(long articleId, long userId);
}
