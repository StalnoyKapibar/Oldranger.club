package ru.java.mentor.oldranger.club.service.article;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticlesSection;

import java.util.List;
import java.util.Set;

public interface ArticleSectionService {

    List<ArticlesSection> getAllArticlesSection();

    Page<Article> getPageableArticlesBySection(ArticlesSection articlesSection, Pageable pageable);

    void addSection(ArticlesSection articlesSection);

    void editSection(ArticlesSection articlesSection);

    void deleteSection(Long id);

    ArticlesSection getSectionById(Long id);

    Set<ArticlesSection> addSectionToSet(List<Long> id);



}
