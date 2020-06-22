package ru.java.mentor.oldranger.club.dao.ForumRepository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Subsection;

import java.util.List;

public interface SubsectionRepository extends JpaRepository<Subsection, Long> {

    @Query("select t from Subsection t where t.isHideToAnon=false")
    List<Subsection> getAllByHideToAnonIsFalse(Sort position);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE subsections SET position = position+1 WHERE position >= ?")
    void updateSubsectionsPosition(int position);

    @Query(nativeQuery = true, value = "SELECT MAX(position) FROM subsections")
    int findMaxPosition();

    List<Subsection> findSubsectionsByName(String name);
}
