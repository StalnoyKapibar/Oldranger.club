package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlackListDto {
    private Long id;
    private String dateUnblock;
}
