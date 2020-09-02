package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.java.mentor.oldranger.club.model.forum.Section;
@Data
@AllArgsConstructor
public class SubsectionDto {

    private Section section;
    private String name;
    private int position;
}
