package ru.java.mentor.oldranger.club.service.forum;

import ru.java.mentor.oldranger.club.dto.SectionsAndSubsectionsDto;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Subsection;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SectionsAndSubsectionsService {

    List<SectionsAndSubsectionsDto> getAllSectionsAndSubsections();

    void swapSections(List<Long> sectionsId);

    void swapSubsections(Map<Long, List<String>> sectionsAndSubsectionsIds);


}
