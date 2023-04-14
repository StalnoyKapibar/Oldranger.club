package ru.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oldranger.club.model.user.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoCommentDto {

    private long positionInPhoto;
    private long photoId;
    private User author;
    private LocalDateTime commentDateTime;
    private Long commentCount;
    private String commentText;

}
