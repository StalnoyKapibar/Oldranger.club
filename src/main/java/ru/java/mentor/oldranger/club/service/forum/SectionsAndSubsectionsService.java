package ru.java.mentor.oldranger.club.service.forum;

import ru.java.mentor.oldranger.club.dto.SectionsAndSubsectionsDto;

import java.util.List;
import java.util.Map;

public interface SectionsAndSubsectionsService {

    List<SectionsAndSubsectionsDto> getAllSectionsAndSubsections();

    void swapSections(List<Long> sectionsId);

    void swapSubsectons(Map<Long, List<String>> sectionsAndSubsectionsIds);

}
