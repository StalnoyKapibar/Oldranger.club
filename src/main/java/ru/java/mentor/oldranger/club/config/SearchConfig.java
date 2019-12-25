package ru.java.mentor.oldranger.club.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.java.mentor.oldranger.club.dao.SearchRepository.Impl.SearchRepositoryImpl;
import ru.java.mentor.oldranger.club.dao.SearchRepository.SearchRepository;
import ru.java.mentor.oldranger.club.service.utils.impl.SearchServiceImpl;
import javax.persistence.*;

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