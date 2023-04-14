package ru.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oldranger.club.model.forum.Section;
import ru.oldranger.club.model.forum.Subsection;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SectionsAndSubsectionsDto {

    private Section section;
    private List<Subsection> subsections;

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
