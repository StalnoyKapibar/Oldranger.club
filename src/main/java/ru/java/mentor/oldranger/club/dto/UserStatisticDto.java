package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserStatisticDto {
    private Long userStatisticId;
    private String nickName;
    private String email;
    private LocalDateTime registered;
    private String role;
    private LocalDateTime lastComment;
    private LocalDateTime lastVizit;
}
