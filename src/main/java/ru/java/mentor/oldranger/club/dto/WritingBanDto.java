package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class WritingBanDto {
    private Long id;
    private String banType;
    private String dateUnblock;
}
