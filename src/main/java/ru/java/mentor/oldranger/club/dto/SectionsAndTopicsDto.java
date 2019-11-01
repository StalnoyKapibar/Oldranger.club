package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Topic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class SectionsAndTopicsDto {
    private Map<Section, List<Topic>> sectionListMap = new HashMap<>();

    public SectionsAndTopicsDto(List<Section> sections, List<Topic> topics) {
        for (Section section : sections) {
            List<Topic> topicList = topics.stream().filter(topic -> topic.getSection().equals(section)).collect(Collectors.toList());
            sectionListMap.put(section, topicList);
        }
    }
}
