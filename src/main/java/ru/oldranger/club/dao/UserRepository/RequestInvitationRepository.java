package ru.oldranger.club.dao.UserRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oldranger.club.model.user.RequestInvitation;

public interface RequestInvitationRepository extends JpaRepository<RequestInvitation, Long> {
}