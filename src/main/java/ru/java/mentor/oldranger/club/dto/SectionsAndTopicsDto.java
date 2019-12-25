package ru.java.mentor.oldranger.club.dto;

import lombok.*;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import java.util.List;

@Data
@AllArgsConstructor
public class SectionsAndTopicsDto {
    private Section section;
    private List<Topic> topics;
}