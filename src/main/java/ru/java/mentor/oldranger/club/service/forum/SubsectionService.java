package ru.java.mentor.oldranger.club.service.forum;

import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.model.forum.Subsection;

import java.util.List;
import java.util.Optional;

@Service
public interface SubsectionService {

    void createSubsection(Subsection subSection);

    public Optional<Subsection> getById(long id);

    List<Subsection> getAllSubsections();
}
