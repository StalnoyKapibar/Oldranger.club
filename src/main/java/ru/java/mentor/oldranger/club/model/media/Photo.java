package ru.java.mentor.oldranger.club.model.media;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_img")
    private String original;

    @Column(name = "small_img")
    private String small;

    @Column(name = "description")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMMM HH:mm", locale = "RU")
    @Column(name = "upload_photo_date")
    private LocalDateTime uploadPhotoDate;

    @ManyToOne
    @JsonBackReference
    PhotoAlbum album;

    @Column(name = "comment_count")
    private Long commentCount;

    public Photo(String original, String small) {
        this.original = original;
        this.small = small;
    }


}
