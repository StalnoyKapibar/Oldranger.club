package ru.java.mentor.oldranger.club.dao.UserRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.user.InvitationToken;

public interface InviteRepository extends JpaRepository<InvitationToken, Long> {
    InvitationToken findByKey(String key);
}
