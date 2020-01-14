package ru.java.mentor.oldranger.club.service.forum;

import ru.java.mentor.oldranger.club.model.forum.Section;

import java.util.List;
import java.util.Optional;

public interface SectionService {

    void addSection(Section section);

    Optional<Section> findById(long id);

    List<Section> getAll();

    List<Section> getAllForAnon();

    void delete(Section section);

    Section update(Section section);

    long countAllByName(String name);

    long countAllByPosition(int position);
}