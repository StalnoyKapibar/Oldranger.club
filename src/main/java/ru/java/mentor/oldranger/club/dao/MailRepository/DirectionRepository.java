package ru.java.mentor.oldranger.club.dao.MailRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.mail.Direction;
import ru.java.mentor.oldranger.club.model.mail.DirectionType;


import java.util.List;

public interface DirectionRepository extends JpaRepository<Direction, Long> {

    List<Direction> getDirectionByDirectionType(DirectionType type);

}
