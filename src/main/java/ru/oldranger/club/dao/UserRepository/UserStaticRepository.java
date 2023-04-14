package ru.oldranger.club.dao.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.oldranger.club.dto.UserStatisticDto;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.model.user.UserStatistic;

public interface UserStaticRepository extends JpaRepository<UserStatistic, Long> {

    @Query(value = "select  new ru.oldranger.club.dto.UserStatisticDto" +
            "(us.id, u.nickName, u.email, u.regDate, r.role, us.lastComment, us.lastVisit)  " +
            "from UserStatistic us join us.user u join u.role r")
    Page<UserStatisticDto> findAllDto(Pageable pageable);

    UserStatistic getOneByUser(User user);

    @Query(value = "select  new ru.oldranger.club.dto.UserStatisticDto" +
            "(us.id, u.nickName, u.email, u.regDate, r.role, us.lastComment, us.lastVisit)  " +
            "from UserStatistic us join us.user u join u.role r where u.firstName=:q or u.email=:q or u.lastName=:q")
    Page<UserStatisticDto> findByQueryDto(Pageable pageable, @Param("q") String query);
}

