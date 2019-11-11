package ru.java.mentor.oldranger.club.dao.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;

public interface UserStaticRepository extends JpaRepository<UserStatistic, Long> {
    Page<UserStatistic> findAll(Pageable pageable);
    UserStatistic getOneByUser(User user);

    @Query(value = "SELECT * FROM user_statistic WHERE user_id IN (" +
            "SELECT id_user FROM users WHERE FIND_IN_SET(LOWER(first_name), :q)" +
            "OR FIND_IN_SET(LOWER(last_name), :q) OR FIND_IN_SET(LOWER(email), :q))", nativeQuery = true)
    Page<UserStatistic> findByQuery(Pageable pageable, @Param("query") String q);
}

