package ru.java.mentor.oldranger.club.service.forum.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dto.SectionsAndSubsectionsDto;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Subsection;
import ru.java.mentor.oldranger.club.service.forum.SectionService;
import ru.java.mentor.oldranger.club.service.forum.SectionsAndSubsectionsService;
import ru.java.mentor.oldranger.club.service.forum.SubsectionService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SectionsAndSubsectionsServiceImpl implements SectionsAndSubsectionsService {

    private SectionService sectionService;

    private SubsectionService subsectionService;

    @Override
    public List<SectionsAndSubsectionsDto> getAllSectionsAndSubsections() {
        List<Section> sections = sectionService.getAllSections();
        List<Subsection> subsections = subsectionService.getAllSubsections();
        return combineListOfSectionsAndSubsections(sections, subsections);
    }






    private List<SectionsAndSubsectionsDto> combineListOfSectionsAndSubsections(List<Section> sections, List<Subsection> subsections) {
        List<SectionsAndSubsectionsDto> dtos = new ArrayList<>();
        for (Section section : sections) {
            List<Subsection> subsectionsList = subsections.stream().filter(subsection -> subsection.getSection().equals(section)).collect(Collectors.toList());
            SectionsAndSubsectionsDto dto = new SectionsAndSubsectionsDto(section, subsectionsList);
            dtos.add(dto);
        }
        return dtos;
    }
}
