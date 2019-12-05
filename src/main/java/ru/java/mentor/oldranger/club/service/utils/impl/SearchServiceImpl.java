package ru.java.mentor.oldranger.club.service.utils.impl;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.java.mentor.oldranger.club.model.forum.Comment;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.service.utils.SearchService;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private final EntityManager entityManager;
    private final FullTextEntityManager fullTextEntityManager;


    @Autowired
    public SearchServiceImpl(final EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
        this.fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
    }

    @PostConstruct
    public void initializeHibernateSearch() {
        try {
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public List searchByTopicName(String queryString) {
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Topic.class).get();

        Query query = queryBuilder
                .keyword()
                .fuzzy()
                .onField("name")
                .matching(queryString)
                .createQuery();

        return fullTextEntityManager.createFullTextQuery(query, Topic.class).getResultList();
    }

    @Transactional
    public List searchByComment(String queryString) {
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Comment.class).get();

        Query query = queryBuilder
                .keyword()
                .fuzzy()
                .onField("commentText")
                .matching(queryString)
                .createQuery();

        return fullTextEntityManager.createFullTextQuery(query, Comment.class).getResultList();
    }
}
