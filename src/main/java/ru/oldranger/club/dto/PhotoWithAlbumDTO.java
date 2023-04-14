package ru.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PhotoWithAlbumDTO {
    private Long photoID;
    private String description;
    private LocalDateTime uploadPhotoDate;
    private Long commentCount;
    private PhotoAlbumDto photoAlbumDto;
}
