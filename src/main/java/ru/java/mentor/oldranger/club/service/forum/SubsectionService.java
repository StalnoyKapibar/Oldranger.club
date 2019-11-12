package ru.java.mentor.oldranger.club.service.forum;

import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.model.forum.Subsection;

@Service
public interface SubsectionService {

    void createSubsection(Subsection subSection);

}
