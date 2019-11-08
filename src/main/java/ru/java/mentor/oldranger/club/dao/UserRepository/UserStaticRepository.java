package ru.java.mentor.oldranger.club.dao.UserRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;

public interface UserStaticRepository extends JpaRepository<UserStatistic, Long> {
}