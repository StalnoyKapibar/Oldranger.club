package ru.oldranger.club.service.user;

import ru.oldranger.club.model.user.PasswordRecoveryToken;

public interface PasswordRecoveryTokenService {

    PasswordRecoveryToken getByUserId(long userId);

    void saveOrUpdateIfExist(PasswordRecoveryToken recoveryToken);

    void delete(PasswordRecoveryToken recoveryToken);

    void save(PasswordRecoveryToken recoveryToken);

    void cleanup();
}