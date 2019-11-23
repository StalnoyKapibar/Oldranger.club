package ru.java.mentor.oldranger.club.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.java.mentor.oldranger.club.service.utils.impl.SearchServiceImpl;
import javax.persistence.*;

@Configuration
public class SearchConfig {

    @Bean
    @Autowired
    SearchServiceImpl searchService(EntityManagerFactory em) {
        SearchServiceImpl hibernateSearchService = new SearchServiceImpl(em);
        hibernateSearchService.initializeHibernateSearch();
        return hibernateSearchService;
    }

}