package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.java.mentor.oldranger.club.model.forum.Topic;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationTopicDTO {
    private List<Topic> topics;
    private Long totalElements;
}
