package ru.oldranger.club.dao.ForumRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oldranger.club.model.mail.Direction;
import ru.oldranger.club.model.user.User;

public interface DirectionRepository extends JpaRepository<Direction, Long> {
    Direction getByUser(User user);
}
