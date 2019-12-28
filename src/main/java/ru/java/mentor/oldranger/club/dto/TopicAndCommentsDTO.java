package ru.java.mentor.oldranger.club.dto;

import lombok.*;
import ru.java.mentor.oldranger.club.model.forum.Topic;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicAndCommentsDTO {

    private Topic topic;
    private List<CommentDto> commentDto;
}
