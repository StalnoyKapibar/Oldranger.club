package ru.java.mentor.oldranger.club.dao.ArticleRepository.Impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleLikeRepositoryEntityManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class ArticleLikeRepositoryEntityManagerImpl implements ArticleLikeRepositoryEntityManager {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addUser(long articleId, long userId) {
        Session session = (Session) entityManager.getDelegate();
        Transaction transaction = session.beginTransaction();
        session.createSQLQuery("insert into like_users(article_id, user_id) values(" + articleId + "," + userId + ")").executeUpdate();
        transaction.commit();
        session.close();
    }

    @Override
    public void removeUser(long articleId, long userId) {
        Session session = (Session) entityManager.getDelegate();
        Transaction transaction = session.beginTransaction();
        session.createSQLQuery("delete from like_users where article_id = " + articleId + " and user_id = " + userId).executeUpdate();
        transaction.commit();
        session.close();
    }
}
