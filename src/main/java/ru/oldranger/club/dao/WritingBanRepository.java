package ru.oldranger.club.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.model.utils.BanType;
import ru.oldranger.club.model.utils.WritingBan;

public interface WritingBanRepository extends JpaRepository<WritingBan, Long> {
    WritingBan findByUserAndBanType(User user, BanType ban);
}