package ru.java.mentor.oldranger.club.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "DTO для передачи фото по умолчанию (самая первая фотография по дате загрузки), а так же общего количества фото и",
        requiredProperties = {"albumId", "albumTitle"})
public class PhotoAlbumDto {
    private Long id;
    private String title;
    private Long thumbImageId;
    private int photosCounter;
}
