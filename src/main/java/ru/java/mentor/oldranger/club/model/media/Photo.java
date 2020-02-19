package ru.java.mentor.oldranger.club.model.media;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
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

    @ManyToOne(fetch = FetchType.LAZY)
    PhotoAlbum album;

    @Column(name = "comment_count")
    private Long commentCount;

    public Photo(String original, String small) {
        this.original = original;
        this.small = small;
    }


}
