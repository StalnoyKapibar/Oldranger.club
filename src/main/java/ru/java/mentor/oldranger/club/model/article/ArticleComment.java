package ru.java.mentor.oldranger.club.model.article;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Indexed
@Table(name = "article_comment")
public class ArticleComment {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "position")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long positionInArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_article")
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_answer_comment")
    private ArticleComment answerTo;

    @Column(columnDefinition = "DATETIME", name = "date_article_comment")
    private LocalDateTime dateTime;

    @Field
    @Type(type = "materialized_clob")
    @Column(name = "text_comment")
    private String commentText;

    @Transient
    private boolean position;

    public ArticleComment(Article article, User user, ArticleComment answerTo, LocalDateTime dateTime, String commentText) {
        this.article = article;
        this.user = user;
        this.answerTo = answerTo;
        this.dateTime = dateTime;
        this.commentText = commentText;
    }

}
