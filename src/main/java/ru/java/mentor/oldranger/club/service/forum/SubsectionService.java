package ru.java.mentor.oldranger.club.service.forum;

import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Subsection;

import java.util.List;
import java.util.Optional;

public interface SubsectionService {

    void add(Subsection subsection);

    Optional<Subsection> getById(long id);

    List<Subsection> getAllSubsections();

    List<Subsection> getAllSubsectionsForAnon();

    List<Subsection> getAllSubsectionsForAnonBySection(Section section);

    List<Subsection> getAllSubsectionsBySection(Section section);

    void deleteSubsection(Subsection subsection);

    Subsection updateSubsection(Subsection subsection);

    long countAllByName(String name);

    long countAllByPosition(int position);


}
