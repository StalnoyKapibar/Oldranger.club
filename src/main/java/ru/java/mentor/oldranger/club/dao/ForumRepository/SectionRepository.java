package ru.java.mentor.oldranger.club.dao.ForumRepository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.java.mentor.oldranger.club.model.forum.Section;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> getAllByIsHideToAnonIsFalse(Sort position);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE sections SET position = position+1 WHERE position >= ?")
    void updateSectionsPosition(int position);

    @Query(nativeQuery = true, value = "SELECT MAX(position) FROM sections")
    int findMaxPosition();

    List<Section> findSectionsByName(String name);
}
