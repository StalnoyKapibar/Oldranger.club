package ru.java.mentor.oldranger.club.model.news;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "news")
public class News {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private NewsTag newsTag;

    @Column(name = "news_date")
    private LocalDateTime date;

    @Column(name = "news_test")
    private String text;

    public News(String title, User user, NewsTag newsTag, LocalDateTime date, String text) {
        this.title = title;
        this.user = user;
        this.newsTag = newsTag;
        this.date = date;
        this.text = text;
    }
}