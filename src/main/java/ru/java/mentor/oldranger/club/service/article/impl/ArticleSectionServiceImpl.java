package ru.java.mentor.oldranger.club.service.article.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleRepository;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleSectionRepository;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticlesSection;
import ru.java.mentor.oldranger.club.service.article.ArticleSectionService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class ArticleSectionServiceImpl implements ArticleSectionService {

    private ArticleSectionRepository articleSectionRepository;
    private ArticleRepository articleRepository;

    @Override
    public List<ArticlesSection> getAllArticlesSection() {
        log.debug("Getting list of all sections");
        List<ArticlesSection> articlesSections = null;
        try {
            articlesSections = articleSectionRepository.findAll();
            log.debug("Returned list of = {} sections", articlesSections.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return articlesSections;
    }

    @Override
    public Page<Article> getPageableArticlesBySection(ArticlesSection articlesSection, Pageable pageable) {
        log.debug("Getting page {} of articles for section with id = {}", pageable.getPageNumber(), articlesSection.getId());
        Page<Article> articlePage = null;
        try {
            articlePage = articleRepository.findAllByArticlesSections(articlesSection, pageable);
            log.debug("Page returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return articlePage;
    }


    @Override
    public void addSection(ArticlesSection articlesSection) {
        log.info("Saving section {}", articlesSection);
        try {
            articleSectionRepository.save(articlesSection);
            log.info("Section saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void editSection(ArticlesSection articlesSection) {
        log.info("Editing section {}", articlesSection);
        try {
            articleSectionRepository.save(articlesSection);
            log.info("Section saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void deleteSection(Long id) {
        log.info("Deleting section with id = {}", id);
        try {
            articleSectionRepository.deleteById(id);
            log.info("Section deleted");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public ArticlesSection getSectionById(Long id) {
        log.debug("Getting section with id = {}", id);
        return articleSectionRepository.findById(id).orElseThrow(() -> new RuntimeException("Did not find section by id - " + id));
    }

    @Override
    public Set<ArticlesSection> addSectionToSet(List<Long> id) {
        return articleSectionRepository.findByIdIn(id);
    }
}
