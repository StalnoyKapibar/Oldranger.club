package ru.java.mentor.oldranger.club.dao.UserRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.model.user.User;

import java.util.List;

public interface UsersTreeRepository extends JpaRepository <User, Long> {


    @Query(nativeQuery = true, value = "SELECT * FROM invitation_token WHERE user = :userId")
    List<User> getInvitedUsersTreeById(Long userId);

}
