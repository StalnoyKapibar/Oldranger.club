package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.java.mentor.oldranger.club.model.forum.Subsection;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubsectionPageDTO {
    private List<SectionsAndSubsectionsDto> topics;
    private Long totalElements;
}
