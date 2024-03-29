package ru.oldranger.club.service.forum;

import ru.oldranger.club.model.forum.Section;

import java.util.List;
import java.util.Optional;

public interface SectionService {

    void addSection(Section section);

    Optional<Section> getById(long id);

    List<Section> getAllSections();

    List<Section> getAllSectionsForAnon();
}