package ru.java.mentor.oldranger.club.service.user;

import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryNoSuchUser;
import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryTokenExpired;
import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryInvalidToken;
import ru.java.mentor.oldranger.club.model.user.User;

public interface PasswordRecoveryService {
    void sendRecoveryTokenToEmail(User user);
    void updatePasswordForUserWithToken(String recoveryToken) throws PasswordRecoveryInvalidToken, PasswordRecoveryTokenExpired, PasswordRecoveryNoSuchUser;
}
