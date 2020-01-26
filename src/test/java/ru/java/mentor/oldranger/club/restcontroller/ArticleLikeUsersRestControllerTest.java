package ru.java.mentor.oldranger.club.restcontroller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import ru.java.mentor.oldranger.club.AbstractSpringTest;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.article.ArticleService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ArticleLikeUsersRestControllerTest extends AbstractSpringTest {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private TestEntityManager testEntityManager;


    @Test
    void makeLike() {

    }

    @Test
    @Transactional
    void getLikeUsersByArticlesId() throws Exception {
        User user = new User();
        user.setEmail("test@demkiv.com");
        user.setNickName("YanochKa");
        Article article = new Article();
        article.setLikes(Collections.singleton(user));

        testEntityManager.persist(article);
        testEntityManager.persist(user);
        testEntityManager.flush();

        String contentAsString = mockMvc
                .perform(get(String.format("/api/article/%d/likes", article.getId())))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(Integer.parseInt(contentAsString), 1);
    }
}