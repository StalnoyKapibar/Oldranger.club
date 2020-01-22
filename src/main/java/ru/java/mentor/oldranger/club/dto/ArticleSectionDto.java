package ru.java.mentor.oldranger.club.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.article.ArticlesSection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleSectionDto {

    private ArticlesSection articlesSection;
    private Page<Article> articles;
}
