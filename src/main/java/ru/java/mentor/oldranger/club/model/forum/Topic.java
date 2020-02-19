package ru.java.mentor.oldranger.club.model.forum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Indexed
@Table(name = "topics")
public class Topic {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Field
    @Column(name = "name_topic", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User topicStarter;

    @Column(name = "message_count")
    private long messageCount;

    @Column(name = "date_start")
    private LocalDateTime startTime;

    @Column(name = "date_last_message")
    private LocalDateTime lastMessageTime;

    @Column(name = "start_message", columnDefinition = "varchar(8000)")
    private String startMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subsection_id")
    private Subsection subsection;

    @Column(name = "is_hide", columnDefinition = "TINYINT")
    private boolean isHideToAnon;

    @Column(name = "forbid_add_update_comment", columnDefinition = "TINYINT")
    private boolean isForbidComment;

    @OneToOne(fetch = FetchType.LAZY)
    private PhotoAlbum photoAlbum;

    public PhotoAlbum getPhotoAlbum() {
        return photoAlbum;
    }

    public void setPhotoAlbum(PhotoAlbum photoAlbum) {
        this.photoAlbum = photoAlbum;
    }

    public Topic(String name, User topicStarter, LocalDateTime startTime, LocalDateTime lastMessageTime
            , Subsection subsection, boolean isHideToAnon, boolean isForbidComment) {
        this.name = name;
        this.topicStarter = topicStarter;
        this.startTime = startTime;
        this.lastMessageTime = lastMessageTime;
        this.subsection = subsection;
        this.isHideToAnon = isHideToAnon;
        this.startMessage = "Default start message";
        this.isForbidComment = isForbidComment;
    }

    public Topic(String name, User topicStarter, LocalDateTime startTime, String startMessage
            , LocalDateTime lastMessageTime, Subsection subsection, boolean isHideToAnon
            , boolean isForbidComment) {
        this.name = name;
        this.topicStarter = topicStarter;
        this.startTime = startTime;
        this.lastMessageTime = lastMessageTime;
        this.subsection = subsection;
        this.isHideToAnon = isHideToAnon;
        this.startMessage = startMessage;
        this.isForbidComment = isForbidComment;
    }

    public Topic(String name, User topicStarter, LocalDateTime startTime, LocalDateTime lastMessageTime
            , Subsection subsection, boolean isHideToAnon, boolean isForbidComment, PhotoAlbum photoAlbum) {
        this.name = name;
        this.topicStarter = topicStarter;
        this.startTime = startTime;
        this.lastMessageTime = lastMessageTime;
        this.subsection = subsection;
        this.isHideToAnon = isHideToAnon;
        this.startMessage = "Default start message";
        this.isForbidComment = isForbidComment;
        this.photoAlbum = photoAlbum;
    }

    public Topic(String name, User topicStarter, LocalDateTime startTime, String startMessage
            , LocalDateTime lastMessageTime, Subsection subsection, boolean isHideToAnon
            , boolean isForbidComment, PhotoAlbum photoAlbum) {
        this.name = name;
        this.topicStarter = topicStarter;
        this.startTime = startTime;
        this.lastMessageTime = lastMessageTime;
        this.subsection = subsection;
        this.isHideToAnon = isHideToAnon;
        this.startMessage = startMessage;
        this.isForbidComment = isForbidComment;
        this.photoAlbum = photoAlbum;
    }

    public Section getSection() {
        return getSubsection().getSection();
    }

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", topicStarter=" + topicStarter +
                ", messageCount=" + messageCount +
                ", startTime=" + startTime +
                ", lastMessageTime=" + lastMessageTime +
                ", startMessage='" + startMessage + '\'' +
                ", subsection=" + subsection +
                ", isHideToAnon=" + isHideToAnon +
                ", isForbidComment=" + isForbidComment +
                '}';
    }

    public boolean isHideToAnon() {
        return isHideToAnon;
    }

}