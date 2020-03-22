package ru.java.mentor.oldranger.club.model.comment;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.annotations.Indexed;
import ru.java.mentor.oldranger.club.model.article.Article;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Indexed
@Table(name = "article_comment")
@NoArgsConstructor
@AllArgsConstructor
public class ArticleComment extends BaseComment {

    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_article")
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_answer_comment")
    private ArticleComment answerTo;

    public ArticleComment(Article article, User user, ArticleComment answerTo, LocalDateTime dateTime, String commentText) {
        super(user, dateTime, commentText);
        this.article = article;
        this.answerTo = answerTo;
    }

}
