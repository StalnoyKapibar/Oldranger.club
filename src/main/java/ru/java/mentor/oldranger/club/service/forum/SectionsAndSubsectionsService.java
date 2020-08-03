package ru.java.mentor.oldranger.club.service.forum;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.java.mentor.oldranger.club.dto.SectionsAndSubsectionsDto;
import ru.java.mentor.oldranger.club.model.forum.Subsection;

import java.util.List;
import java.util.Map;

public interface SectionsAndSubsectionsService {

    List<SectionsAndSubsectionsDto> getAllSectionsAndSubsections();

    List<SectionsAndSubsectionsDto> getAllSectionsAndSubsectionsForAnon();

    void swapSections(List<Long> sectionsId);

    void swapSubsectons(Map<Long, List<String>> sectionsAndSubsectionsIds);

    Subsection getSubsectionById(Long id);

    Page<Subsection> getAllSabsection (Pageable pageable);
}
