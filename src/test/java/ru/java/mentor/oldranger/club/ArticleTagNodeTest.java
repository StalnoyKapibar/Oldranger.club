package ru.java.mentor.oldranger.club;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;
import ru.java.mentor.oldranger.club.service.article.ArticleTagsNodeService;

@SpringBootTest
public class ArticleTagNodeTest {
    @Autowired
    private ArticleTagsNodeService articleTagsNodeService;

    @Test
    public void articleTagNodeTest() {
        articleTagsNodeService.findAll();
        articleTagsNodeService.findAll();
        articleTagsNodeService.findHierarchyTreeOfAllTagsNodes();
        articleTagsNodeService.findHierarchyTreeOfAllTagsNodes();
        articleTagsNodeService.save(new ArticleTagsNode());
        articleTagsNodeService.findHierarchyTreeOfAllTagsNodes();
        articleTagsNodeService.findAll();
        articleTagsNodeService.findById(1L);
        articleTagsNodeService.findById(1L);
        articleTagsNodeService.deleteById(1L);
        articleTagsNodeService.findById(1L);


    }
}
