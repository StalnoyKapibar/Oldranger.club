package ru.java.mentor.oldranger.club.model.article;

import lombok.*;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "articles")
public class Article {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(cascade = {CascadeType.REFRESH}, fetch=FetchType.EAGER)
    @JoinTable(name = "article_tags",
            joinColumns = { @JoinColumn(name = "article_id") },
            inverseJoinColumns = { @JoinColumn(name = "tag_id") })
    private Set<ArticleTag> articleTags;

    @ManyToMany(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "article_sections",
            joinColumns = { @JoinColumn(name = "article_id") },
            inverseJoinColumns = { @JoinColumn(name = "section_id") })
    private Set<ArticlesSection> articlesSections;

    @Column(name = "article_date")
    private LocalDateTime date;

    @Column(name = "comment_count")
    private long commentCount;

    @Column(name = "article_text")
    private String text;

    @Column(name = "article_hide", columnDefinition = "TINYINT")
    private boolean isHideToAnon;

    public Article(String title, User user, Set<ArticleTag> articleTags, LocalDateTime date, String text, boolean isHideToAnon) {
        this.title = title;
        this.user = user;
        this.articleTags = articleTags;
        this.date = date;
        this.text = text;
        this.isHideToAnon = isHideToAnon;
    }

    public Article(String title, User user, Set<ArticleTag> articleTags, Set<ArticlesSection> articlesSections, LocalDateTime date, String text, boolean isHideToAnon) {
        this.title = title;
        this.user = user;
        this.articleTags = articleTags;
        this.date = date;
        this.text = text;
        this.isHideToAnon = isHideToAnon;
        this.articlesSections = articlesSections;
    }
}