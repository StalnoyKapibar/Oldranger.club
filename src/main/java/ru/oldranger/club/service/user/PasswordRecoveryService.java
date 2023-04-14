package ru.oldranger.club.service.user;

import ru.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryIntervalViolation;
import ru.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryInvalidToken;
import ru.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryTokenExpired;
import ru.oldranger.club.model.user.PasswordRecoveryToken;
import ru.oldranger.club.model.user.User;

public interface PasswordRecoveryService {

    void sendRecoveryTokenToEmail(User user) throws PasswordRecoveryIntervalViolation;

    PasswordRecoveryToken validateToken(String recoveryToken) throws PasswordRecoveryInvalidToken, PasswordRecoveryTokenExpired;

    void updatePassword(PasswordRecoveryToken recoveryToken, String password);
}