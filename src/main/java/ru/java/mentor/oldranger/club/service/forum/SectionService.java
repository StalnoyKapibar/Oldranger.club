package ru.java.mentor.oldranger.club.service.forum;

import ru.java.mentor.oldranger.club.dto.SectionDto;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Subsection;

import java.util.List;
import java.util.Optional;

public interface SectionService {

    SectionDto addSection(Section section);

    Optional<Section> getById(long id);

    List<Section> getAllSections();

    List<Section> getAllSectionsForAnon();

    void updateSectionsPosition(int position);

    int findMaxPosition();

    List<Section> findSectionsByName(String name);
}