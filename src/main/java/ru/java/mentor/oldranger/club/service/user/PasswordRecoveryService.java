package ru.java.mentor.oldranger.club.service.user;

import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryIntervalViolation;
import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryInvalidToken;
import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryTokenExpired;
import ru.java.mentor.oldranger.club.model.user.PasswordRecoveryToken;
import ru.java.mentor.oldranger.club.model.user.User;

public interface PasswordRecoveryService {

    void sendRecoveryTokenToEmail(User user) throws PasswordRecoveryIntervalViolation;

    PasswordRecoveryToken validateToken(String recoveryToken) throws PasswordRecoveryInvalidToken, PasswordRecoveryTokenExpired;

    void updatePassword(PasswordRecoveryToken recoveryToken, String password);
}