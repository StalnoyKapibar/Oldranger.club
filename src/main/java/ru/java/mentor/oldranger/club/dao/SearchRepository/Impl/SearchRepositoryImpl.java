package ru.java.mentor.oldranger.club.dao.SearchRepository.Impl;

import org.apache.lucene.search.Query;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.transaction.annotation.Transactional;
import ru.java.mentor.oldranger.club.dao.SearchRepository.SearchRepository;
import ru.java.mentor.oldranger.club.model.forum.Topic;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;


public class SearchRepositoryImpl implements SearchRepository {
    private FullTextEntityManager fullTextEntityManager;

    public SearchRepositoryImpl() {
    }

    public void init(EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
    }

    @PostConstruct
    public void initializeHibernateSearch() {
        try {

            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param finderTag      - Слово ключ для поиска в БД.
     * @param fetchingFields - Массив названия полей для замены fetch.
     * @param aClass         - Класс но основе которого будет проходить поиск сущностей.
     * @return
     */

    @Transactional
    @Override
    public List searchObjectsByName(String finderTag, String[] fetchingFields, Class aClass) {
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(aClass).get();
        Session session = fullTextEntityManager.unwrap(Session.class);
        String field;
        try {
            if (Class.forName("Comment").equals(aClass)) {
                field = "commentText";
            } else {
                field = "name";
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Criteria fetch = session.createCriteria(aClass);
        if (fetchingFields != null) {
            for (String fetchField : fetchingFields) {
                fetch.setFetchMode(fetchField, FetchMode.JOIN);
            }
        }
        Query query = queryBuilder
                .keyword()
                .fuzzy()
                .onField(field)
                .matching(finderTag)
                .createQuery();
        return fullTextEntityManager
                .createFullTextQuery(query, Topic.class)
                .setCriteriaQuery(fetch)
                .getResultList();
    }
}
