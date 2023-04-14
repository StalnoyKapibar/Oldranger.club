package ru.oldranger.club.dao.UserRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oldranger.club.model.user.InvitationToken;
import ru.oldranger.club.model.user.User;

import java.util.List;

public interface InviteRepository extends JpaRepository<InvitationToken, Long> {
    InvitationToken findByKey(String key);

    List<InvitationToken> findAllByUserAndUsedAndMail(User user, Boolean used, String mail);

    List<InvitationToken> findAllByMailAndUsed(String mail, Boolean used);

    boolean existsByKey(String key);
}
