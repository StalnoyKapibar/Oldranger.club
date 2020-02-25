package ru.java.mentor.oldranger.club.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;

@Data
@AllArgsConstructor
@Schema(description = "DTO для передачи фото по умолчанию (самая первая фотография по дате загрузки), а так же общего количества фото и",
        requiredProperties = {"albumId", "albumTitle"})
public class PhotoAlbumDto {
    private Long photoAlbumId;
    private String title;
    private String originalThumbImage;
    private String smallThumbImage;
    private int photosCounter;
}
