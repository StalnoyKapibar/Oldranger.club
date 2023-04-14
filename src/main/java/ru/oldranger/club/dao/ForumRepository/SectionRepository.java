package ru.oldranger.club.dao.ForumRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oldranger.club.model.forum.Section;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> getAllByIsHideToAnonIsFalse();
}
