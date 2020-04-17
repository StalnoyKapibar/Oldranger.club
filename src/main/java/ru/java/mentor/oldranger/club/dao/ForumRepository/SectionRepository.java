package ru.java.mentor.oldranger.club.dao.ForumRepository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.forum.Section;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> getAllByIsHideToAnonIsFalse(Sort position);
}
