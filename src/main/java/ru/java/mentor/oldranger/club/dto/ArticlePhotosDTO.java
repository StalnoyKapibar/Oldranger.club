package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.java.mentor.oldranger.club.model.media.Photo;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticlePhotosDTO extends ArticleCommentDto {
    private List<Photo> photos;
}

