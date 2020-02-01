package ru.java.mentor.oldranger.club.dao.ForumRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.forum.Subsection;

public interface SubsectionRepository extends JpaRepository<Subsection, Long> {
}
