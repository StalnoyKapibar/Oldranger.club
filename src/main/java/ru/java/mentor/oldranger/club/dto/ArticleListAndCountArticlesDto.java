package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.java.mentor.oldranger.club.model.article.Article;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleListAndCountArticlesDto {

    private List<Article> articleList;
    private long countArticles;
}
