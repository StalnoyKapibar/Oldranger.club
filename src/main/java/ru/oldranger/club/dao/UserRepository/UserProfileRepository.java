package ru.oldranger.club.dao.UserRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.model.user.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    UserProfile getOneByUser(User user);
}