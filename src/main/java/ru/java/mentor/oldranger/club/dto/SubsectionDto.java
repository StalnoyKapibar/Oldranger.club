package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.java.mentor.oldranger.club.model.forum.Section;

@Getter
@Setter
@AllArgsConstructor
public class SubsectionDto {
    private String name;
    private int position;
    private boolean isHideToAnon;
    private Section section;
}
