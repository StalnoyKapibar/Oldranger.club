package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Subsection;

import java.util.List;

@Data
@AllArgsConstructor
public class SectionsAndSubsectionsDto {

    private Section section;
    private List<Subsection> subsections;
}
