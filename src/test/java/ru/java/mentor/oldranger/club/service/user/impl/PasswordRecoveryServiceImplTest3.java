package ru.java.mentor.oldranger.club.service.user.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import lombok.NonNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import ru.java.mentor.oldranger.club.model.user.PasswordRecoveryToken;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.mail.MailService;
import ru.java.mentor.oldranger.club.service.mail.impl.MailServiceImpl;
import ru.java.mentor.oldranger.club.service.user.PasswordRecoveryTokenService;
import ru.java.mentor.oldranger.club.service.user.UserService;

import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;

import org.mockito.internal.util.reflection.FieldSetter;


import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
//@RunWith(PowerMockRunner.class)

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(fullyQualifiedNames = "ru.java.mentor.oldranger.club.service.user.impl.PasswordRecoveryServiceImpl")
class PasswordRecoveryServiceImplTest3 {
    @Mock
    private MailService mailService = Mockito.mock(MailServiceImpl.class);
    @Mock
    private UserService userService = Mockito.mock(UserServiceImpl.class);
    @Mock
    private PasswordEncoder passwordEncoder = Mockito.mock(BCryptPasswordEncoder.class);
    @Mock
    private PasswordRecoveryTokenService passwordRecoveryTokenService = Mockito.mock(PasswordRecoveryTokenServiceImpl.class);

    private PasswordRecoveryServiceImpl passwordRecoveryService;

    private String JWT_SECRET = "secret";
    private String TOKEN_CLAIM = "userid";
    private String PASSWORD_RECOVERY_TOKEN_EXPIRATION_PATTERN
            = "1-12-30";
    private String PASSWORD_RECOVERY_INTERVAL = "0-0-2";
    private User user;

    @BeforeEach
    void initSomeCraziestUseCase() {
        user = new User();
        user.setId(1L);
        passwordRecoveryService = new PasswordRecoveryServiceImpl(mailService, userService,
                passwordEncoder, passwordRecoveryTokenService);
        settingSomeConstants(PASSWORD_RECOVERY_TOKEN_EXPIRATION_PATTERN,
                TOKEN_CLAIM, JWT_SECRET, PASSWORD_RECOVERY_INTERVAL);
    }

    @Test
    void updatePassword() {
        PasswordRecoveryToken token = new PasswordRecoveryToken();
        token.setUser(user);

        Mockito.when(passwordEncoder.encode("xxx")).thenReturn(null);
        Mockito.doNothing().when(userService).save(ArgumentMatchers.any(User.class));
        Mockito.doNothing().when(passwordRecoveryTokenService).delete(token);

        passwordRecoveryService.updatePassword(token, "xxx");

        verify(userService, Mockito.times(1)).save(ArgumentMatchers.any(User.class));
        verify(passwordRecoveryTokenService, Mockito.times(1)).delete(token);
    }

    @Test
    void sendRecoveryTokenToEmail_01_whenTokenDoesntExist() throws Exception {
                verifyTokenCreationOrUpdating_and_MailSendingWhenTokenIs(null);

    }

    @Test
    void sendRecoveryTokenToEmail_02_whenTokenExists() {
        PasswordRecoveryToken token = returnTokenWithIssueDateSoManyDaysAgo(-1000);
        LocalDateTime oldIssueDate = token.getIssueDate();
        LocalDateTime oldExpirationDate = token.getExpirationDate();
        verifyTokenCreationOrUpdating_and_MailSendingWhenTokenIs(token);
        Assert.assertTrue(oldIssueDate.isBefore(token.getIssueDate()));
        Assert.assertTrue(oldExpirationDate.isBefore(token.getExpirationDate()));
        Assert.assertNotEquals(token.getIssueDate().toString(), token.getToken());
    }

    PasswordRecoveryToken returnTokenWithIssueDateSoManyDaysAgo(int days) {
        PasswordRecoveryToken token = new PasswordRecoveryToken();
        token.setUser(user);
        LocalDateTime oldDate = LocalDateTime.now().plusDays(days);
        token.setIssueDate(oldDate);
        token.setExpirationDate(oldDate);
        token.setToken(oldDate.toString());
        return token;
    }

    void mockPasswordRecoveryTokenServiceGetByUserIdWithToken(PasswordRecoveryToken token) {
        when(passwordRecoveryTokenService.getByUserId(ArgumentMatchers.anyLong())).thenReturn(token);
    }

    void settingSomeConstants(String prt, String tc, String js, String pr) {
        ReflectionTestUtils.setField(passwordRecoveryService,
                "PASSWORD_RECOVERY_TOKEN_EXPIRATION_PATTERN",
                prt, String.class);
        ReflectionTestUtils.setField(passwordRecoveryService,
                "TOKEN_CLAIM",
                tc, String.class);
        ReflectionTestUtils.setField(passwordRecoveryService,
                "JWT_SECRET",
                js, String.class);
        ReflectionTestUtils.setField(passwordRecoveryService,
                "PASSWORD_RECOVERY_INTERVAL",
                pr, String.class);


    }


    void verifyTokenCreationOrUpdating_and_MailSendingWhenTokenIs(PasswordRecoveryToken token) {
        mockPasswordRecoveryTokenServiceGetByUserIdWithToken(token);
        try {
            passwordRecoveryService.sendRecoveryTokenToEmail(user);
        } catch (Exception e) {
            fail("fail testing sendRecoveryTokenToEmail with token: " + token);
        }
        verify(passwordRecoveryTokenService, Mockito.times(1))
                .save(ArgumentMatchers.any(PasswordRecoveryToken.class));
        verify(mailService, Mockito.times(1)).send(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any());
    }

    private String createJwtToken(long userId, Date expirationTime, String tokenClaim, String jwtSecret) {
        String token = null;
        try {
            token = JWT
                    .create()
                    .withClaim(tokenClaim, userId)
                    .withExpiresAt(expirationTime)
                    .sign(Algorithm.HMAC512(jwtSecret));
        } catch (JWTDecodeException e) {
            System.out.println(e.getMessage());
        }
        return token;
    }

}