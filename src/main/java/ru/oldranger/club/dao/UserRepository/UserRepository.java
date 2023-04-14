package ru.oldranger.club.dao.UserRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.oldranger.club.model.user.Role;
import ru.oldranger.club.model.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAll();

    User findUserByNickName(String name);

    User findUserByEmail(String email);

    List<User> findAllByRegDateBeforeAndRoleNotAndRoleNotAndRoleNot(LocalDateTime localDateTime, Role roleAdmin, Role roleVeteran, Role roleModerator);

    @Query(value = "SELECT * FROM users WHERE nick_name=:q or email=:q LIMIT 1", nativeQuery = true)
    Optional<User> findUserByEmailOrNickName(@Param("q") String login);

    @Query(value = "SELECT * from users WHERE invite_key=:q LIMIT 1", nativeQuery = true)
    Optional<User> findUserByInviteKey(@Param("q") String key);
}
