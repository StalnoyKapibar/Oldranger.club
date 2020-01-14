package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SectionDto {
    private String name;
    private int position;
    private boolean isHideToAnon;
}
