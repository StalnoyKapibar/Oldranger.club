package ru.java.mentor.oldranger.club.dao.UserRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.user.EmailChangeToken;

public interface EmailChangeRepository extends JpaRepository<EmailChangeToken, Long> {
    EmailChangeToken findByKey(String key);
}
