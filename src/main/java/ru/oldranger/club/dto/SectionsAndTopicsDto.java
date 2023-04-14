package ru.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.oldranger.club.model.forum.Section;
import ru.oldranger.club.model.forum.Topic;

import java.util.List;

@Data
@AllArgsConstructor
public class SectionsAndTopicsDto {
    private Section section;
    private List<Topic> topics;


    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }
}