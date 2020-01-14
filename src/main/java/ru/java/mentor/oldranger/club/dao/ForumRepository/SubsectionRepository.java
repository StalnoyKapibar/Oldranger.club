package ru.java.mentor.oldranger.club.dao.ForumRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Subsection;

import java.util.List;

public interface SubsectionRepository extends JpaRepository<Subsection, Long> {

    List<Subsection> getAllByHideToAnonIsFalse();

    List<Subsection> getAllByHideToAnonIsFalseAndSection( Section section);

    List<Subsection> getAllBySection(Section section);

    void deleteAllBySection(Section section);

    long countAllByPosition(int position);

    long countAllByName(String name);
}
