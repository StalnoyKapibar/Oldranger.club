package ru.java.mentor.oldranger.club.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;

@Data
@AllArgsConstructor
//ToDo дописать description и requiredProperties
@Schema(description = "...ToDo... ",
        requiredProperties = {".ToDo..", ".ToDo.."})
public class PhotoAlbumDto {
    private PhotoAlbum album;
    private Photo mainPhoto;
}
