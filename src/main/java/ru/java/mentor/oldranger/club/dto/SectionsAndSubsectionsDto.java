package ru.java.mentor.oldranger.club.dto;

import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Subsection;

import java.util.List;

public class SectionsAndSubsectionsDto {

    private Section section;
    private List<Subsection> subsections;

    public SectionsAndSubsectionsDto() {}

    public SectionsAndSubsectionsDto(Section section, List<Subsection> subsections) {
        this.section = section;
        this.subsections = subsections;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public List<Subsection> getSubsections() {
        return subsections;
    }

    public void setSubsections(List<Subsection> subsections) {
        this.subsections = subsections;
    }
}
