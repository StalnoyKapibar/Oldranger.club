package ru.java.mentor.oldranger.club.dao.ForumRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.forum.Theme;

public interface ThemeRepository extends JpaRepository<Theme, Long> {
}
