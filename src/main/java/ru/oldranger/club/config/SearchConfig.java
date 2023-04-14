package ru.oldranger.club.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.oldranger.club.dao.SearchRepository.Impl.SearchRepositoryImpl;
import ru.oldranger.club.dao.SearchRepository.SearchRepository;

import javax.persistence.EntityManagerFactory;

@Configuration
public class SearchConfig {

    @Bean
    @Autowired
    SearchRepository searchService(EntityManagerFactory em) {
        SearchRepositoryImpl searchRepository = new SearchRepositoryImpl();
        searchRepository.init(em);
        searchRepository.initializeHibernateSearch();
        return searchRepository;
    }

}