package ru.java.mentor.oldranger.club.service.user;

import ru.java.mentor.oldranger.club.model.user.PasswordRecoveryToken;

public interface PasswordRecoveryTokenService {

    PasswordRecoveryToken getByUserId(long userId);

    void saveOrUpdateIfExist(PasswordRecoveryToken recoveryToken);

    void delete(PasswordRecoveryToken recoveryToken);

    void save(PasswordRecoveryToken recoveryToken);

    void cleanup();
}