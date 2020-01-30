package ru.java.mentor.oldranger.club.dao.UserRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.java.mentor.oldranger.club.model.user.User;

import java.util.List;

public interface UsersTreeRepository extends JpaRepository<User, Long> {


    @Query(nativeQuery = true, value = "select * from users where id_user in (select new_user from invitation_token where user = :userId)")
    List<User> getInvitedUsersTreeById(@Param("userId") long userId);
}