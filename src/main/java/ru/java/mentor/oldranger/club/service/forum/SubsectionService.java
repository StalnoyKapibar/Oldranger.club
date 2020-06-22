package ru.java.mentor.oldranger.club.service.forum;

import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dto.SubsectionDto;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Subsection;

import java.util.List;
import java.util.Optional;

@Service
public interface SubsectionService {

   SubsectionDto createSubsection(Subsection subSection);

    Optional<Subsection> getById(long id);

    List<Subsection> getAllSubsections();

    void updateSubsectionsPosition(int position);

    int findMaxPosition();

    List<Subsection> findSubsectionsByName(String name);
}
