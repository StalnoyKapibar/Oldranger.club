package ru.oldranger.club.model.comment;

import lombok.AllArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import ru.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@MappedSuperclass
public abstract class BaseComment {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private User user;

    @Column(columnDefinition = "DATETIME", name = "date_comment")
    private LocalDateTime dateTime;

    @Column(name = "position")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long position;

    @Field
    @Type(type = "materialized_clob")
    @Column(name = "text_comment")
    private String commentText;

    public BaseComment(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getCommentText() {
        return commentText;
    }

    public BaseComment(User user, LocalDateTime localDateTime, String commentText) {
        this.user = user;
        this.dateTime = localDateTime;
        this.commentText= commentText;
    }

    public BaseComment() {
    }

    public void setDateTime(LocalDateTime localDateTime) {
        this.dateTime = localDateTime;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public Long getPosition() {
        return position;
    }
}