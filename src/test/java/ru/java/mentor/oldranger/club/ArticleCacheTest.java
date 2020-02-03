package ru.java.mentor.oldranger.club;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.service.article.ArticleService;

@SpringBootTest
public class ArticleCacheTest {
    @Autowired
    private ArticleService articleService;
    @Test
    public void testArticleCacheService(){
        //Есть заход в метод
        Article article = articleService.getArticleById(1L);
        //Нет захода в метод
        Article article1 = articleService.getArticleById(1L);
        //Удаление и отчистка кэша
        articleService.deleteArticle(1L);
        //снова заходит в метод
        Article article2 = articleService.getArticleById(1L);

    }
}
