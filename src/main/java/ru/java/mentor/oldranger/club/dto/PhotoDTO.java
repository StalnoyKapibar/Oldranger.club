package ru.java.mentor.oldranger.club.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PhotoDTO {
    private Long photoID;
    private String description;
    private LocalDateTime uploadPhotoDate;
    private Long commentCount;
}
