package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Topic;

import java.util.List;

@AllArgsConstructor
public class SectionsAndTopicsDto {
    private List<Section> sections;
    private List<Topic> topics;
}
