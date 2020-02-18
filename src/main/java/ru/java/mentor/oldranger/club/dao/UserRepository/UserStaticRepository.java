package ru.java.mentor.oldranger.club.dao.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.java.mentor.oldranger.club.dto.UserStatisticDto;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserStatistic;

public interface UserStaticRepository extends JpaRepository<UserStatistic, Long> {

    @Query(nativeQuery = true, value = "select u.id, u.nick_name, u.email, u.registered, r.role, us.last_comment, us.last_visit " +
            " from users as u left join user_statistic as us left join roles as r on us.user_id=u.id and u.role_id=r.id")
    Page<UserStatisticDto> findAllDto(Pageable pageable);

    UserStatistic getOneByUser(User user);

    @Query(value = "SELECT * FROM user_statistic WHERE user_id IN (" +
            "SELECT id_user FROM users WHERE FIND_IN_SET(LOWER(first_name), :q)" +
            "OR FIND_IN_SET(LOWER(last_name), :q) OR FIND_IN_SET(LOWER(email), :q))", nativeQuery = true)
    Page<UserStatisticDto> findByQueryDto(Pageable pageable, @Param("q") String query);
}

