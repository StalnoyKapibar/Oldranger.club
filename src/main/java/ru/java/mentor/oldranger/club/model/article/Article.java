package ru.java.mentor.oldranger.club.model.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "article_tags",
            joinColumns = {@JoinColumn(name = "article_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private Set<ArticleTag> articleTags;

    @Column(name = "article_date")
    private LocalDateTime date;

    @Column(name = "comment_count")
    private long commentCount;

    @Column(name = "article_text", columnDefinition = "MEDIUMTEXT")
    private String text;

    @Column(name = "draft", columnDefinition = "TINYINT")
    private boolean draft;

    @LazyCollection(LazyCollectionOption.EXTRA)
    @ManyToMany(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "like_users",
            joinColumns = {@JoinColumn(name = "article_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Set<User> likes = new HashSet<>();

    public Article(String title, User user, Set<ArticleTag> articleTags, LocalDateTime date, String text, boolean draft) {
        this.title = title;
        this.user = user;
        this.articleTags = articleTags;
        this.date = date;
        this.text = text;
        this.draft = draft;
    }

}