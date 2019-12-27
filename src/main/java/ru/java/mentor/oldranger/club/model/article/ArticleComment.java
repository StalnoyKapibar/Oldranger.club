package ru.java.mentor.oldranger.club.model.article;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import ru.java.mentor.oldranger.club.model.user.User;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
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

    public ArticleComment getAnswerTo() {
        return answerTo;
    }

    public void setAnswerTo(ArticleComment answerTo) {
        this.answerTo = answerTo;
    }

    public boolean isPosition() {
        return position;
    }

    public void setPosition(boolean position) {
        this.position = position;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArticleComment that = (ArticleComment) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "ArticleComment{" +
                "id=" + id +
                ", positionInArticle=" + positionInArticle +
                ", article=" + article +
                ", user=" + user +
                ", answerTo=" + answerTo +
                ", dateTime=" + dateTime +
                ", commentText='" + commentText + '\'' +
                ", position=" + position +
                '}';
    }
}
