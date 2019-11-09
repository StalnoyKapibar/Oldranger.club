package ru.java.mentor.oldranger.club.dao.UserRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.model.user.PasswordRecoveryToken;

import java.time.LocalDateTime;
import java.util.List;

public interface PasswordRecoveryTokenRepository extends JpaRepository<PasswordRecoveryToken, Long> {

    @Query("select t from PasswordRecoveryToken t where t.user.id=:userId")
    PasswordRecoveryToken getByUserId(long userId);

    @Modifying
    @Query(nativeQuery = true,
            value = "INSERT INTO password_recovery_tokens (user_id, token, issue_date) VALUES(?1, ?2, ?3) ON DUPLICATE KEY UPDATE token=?2, issue_date=?3")
    void saveOrUpdateIfExist(Long userId, String token, LocalDateTime issueDate);

    @Modifying
    @Query("delete from PasswordRecoveryToken t where t.expirationDate<=:expirationDateTime")
    void cleanup(LocalDateTime expirationDateTime);

    @Query("select t from PasswordRecoveryToken t where t.expirationDate>:expirationDateTime")
    List<PasswordRecoveryToken> validTokens(LocalDateTime expirationDateTime);
}