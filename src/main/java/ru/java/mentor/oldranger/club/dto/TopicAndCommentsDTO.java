package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import ru.java.mentor.oldranger.club.model.forum.Topic;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicAndCommentsDTO {

    private Topic topic;
    private boolean isSubscribed;
    private Page<CommentDto> commentDto;
}
