package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import ru.java.mentor.oldranger.club.model.media.Photo;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoAndCommentsDTO {

    private Photo photo;
    private Page<PhotoCommentDto> commentDto;
}
