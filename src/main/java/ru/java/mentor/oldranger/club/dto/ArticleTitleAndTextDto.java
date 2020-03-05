package ru.java.mentor.oldranger.club.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleTitleAndTextDto {
    private String title;
    private String text;
}
