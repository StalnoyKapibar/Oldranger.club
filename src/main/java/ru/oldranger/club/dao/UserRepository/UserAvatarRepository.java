package ru.oldranger.club.dao.UserRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oldranger.club.model.user.UserAvatar;

public interface UserAvatarRepository extends JpaRepository<UserAvatar, Long> {
}