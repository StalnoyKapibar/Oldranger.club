package ru.java.mentor.oldranger.club.service.forum;

import ru.java.mentor.oldranger.club.model.forum.Section;

import java.util.List;
import java.util.Optional;

public interface SectionService {

    Section addSection(Section section);

    Optional<Section> getById(long id);

    List<Section> getAllSections();

    List<Section> getAllSectionsForAnon();
}