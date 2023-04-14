package ru.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WritingBanDto {
    private Long id;
    private String banType;
    private String dateUnblock;
}
