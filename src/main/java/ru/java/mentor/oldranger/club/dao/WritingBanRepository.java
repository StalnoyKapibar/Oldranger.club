package ru.java.mentor.oldranger.club.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BanType;
import ru.java.mentor.oldranger.club.model.utils.WritingBan;

import java.util.List;

public interface WritingBanRepository extends JpaRepository<WritingBan, Long> {
    WritingBan findByUserAndBanType(User user, BanType ban);

    @Query(value = "select us.banType from WritingBan us join us.user u join u.role r where u.id=:i")
    List<BanType> findByUserId(@Param("i") Long id);
}