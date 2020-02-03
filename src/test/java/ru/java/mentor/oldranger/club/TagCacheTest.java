package ru.java.mentor.oldranger.club;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;
import ru.java.mentor.oldranger.club.service.article.ArticleTagsNodeService;

@SpringBootTest
public class TagCacheTest {
    @Autowired
    private ArticleTagsNodeService articleTagsNodeService;
    @Test
    public void tagNodeTestCache() {
        articleTagsNodeService.save(new ArticleTagsNode());
        //Есть заход в метод
        ArticleTagsNode tagsNode = articleTagsNodeService.findById(1L);
        //Нет захода в метод
        ArticleTagsNode tagsNode1 = articleTagsNodeService.findById(1L);
        //Удаление и отчистка кэша
        articleTagsNodeService.deleteById(1L);
        //снова заходит в метод
        ArticleTagsNode tagsNode3 = articleTagsNodeService.findById(1L);
    }
}
