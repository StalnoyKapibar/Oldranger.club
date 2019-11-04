package ru.java.mentor.oldranger.club.dao.UserRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.model.user.PasswordRecoveryToken;
import ru.java.mentor.oldranger.club.model.user.Role;

import java.time.LocalDateTime;

public interface PasswordRecoveryTokenRepository extends JpaRepository<PasswordRecoveryToken, Long> {

    @Query("select t from PasswordRecoveryToken t where t.user.id=:userId")
    PasswordRecoveryToken getByUserId(long userId);

    @Modifying
    @Query(nativeQuery = true,
            value = "INSERT INTO recovery_token (user_id, token, iss_date) VALUES(?1, ?2, ?3) ON DUPLICATE KEY UPDATE token=?2, iss_date=?3")
    void saveOrUpdateIfExist(Long userId, String token, LocalDateTime issueDate);
}
