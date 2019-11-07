package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Topic;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SectionsAndTopicsDto {
    private Section section;
    private List<Topic> topics;
}
