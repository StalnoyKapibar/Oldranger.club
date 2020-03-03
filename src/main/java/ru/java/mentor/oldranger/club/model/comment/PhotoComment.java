package ru.java.mentor.oldranger.club.model.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.annotations.Indexed;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Indexed
@Table(name = "photo_comments")
@NoArgsConstructor
@AllArgsConstructor
public class PhotoComment extends BaseComment {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_photo")
    private Photo photo;

    public PhotoComment(Photo photo, User user, LocalDateTime dateTime, String commentText) {
        super(user, dateTime, commentText);
        this.photo = photo;
    }
}
