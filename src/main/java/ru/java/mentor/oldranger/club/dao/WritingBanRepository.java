package ru.java.mentor.oldranger.club.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BanType;
import ru.java.mentor.oldranger.club.model.utils.WritingBan;

public interface WritingBanRepository extends JpaRepository<WritingBan, Long> {
    WritingBan findByUserAndBanType(User user, BanType ban);
}