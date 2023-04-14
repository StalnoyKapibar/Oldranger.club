package ru.oldranger.club.service.forum;

import ru.oldranger.club.dto.SectionsAndSubsectionsDto;
import ru.oldranger.club.model.forum.Subsection;

import java.util.List;
import java.util.Map;

public interface SectionsAndSubsectionsService {

    List<SectionsAndSubsectionsDto> getAllSectionsAndSubsections();

    void swapSections(List<Long> sectionsId);

    void swapSubsectons(Map<Long, List<String>> sectionsAndSubsectionsIds);

    Subsection getSubsectionById(Long id);
}
