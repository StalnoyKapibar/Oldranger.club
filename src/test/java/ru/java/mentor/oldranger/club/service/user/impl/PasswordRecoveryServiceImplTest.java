package ru.java.mentor.oldranger.club.service.user.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryIntervalViolation;
import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryInvalidToken;
import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryTokenExpired;
import ru.java.mentor.oldranger.club.model.user.PasswordRecoveryToken;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.mail.MailService;
import ru.java.mentor.oldranger.club.service.mail.impl.MailServiceImpl;
import ru.java.mentor.oldranger.club.service.user.PasswordRecoveryTokenService;
import ru.java.mentor.oldranger.club.service.user.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
class PasswordRecoveryServiceImplTest {
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
    void sendRecoveryTokenToEmail_01_whenTokenDoesntExist()  {
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

    @Test
    void sendRecoveryTokenToEmail_03_whenPasswordRecoveryIntervalViolations() {
        Mockito.when(passwordRecoveryTokenService.getByUserId(user.getId())).thenReturn(returnTokenWithIssueDateSoManyDaysAgo(1000));

        assertThrows(PasswordRecoveryIntervalViolation.class,
                () -> passwordRecoveryService.sendRecoveryTokenToEmail(user),
                "NO Password Recovery Interval Violations FOUND !!!!");
    }


    @Test
    void validateToken_00_privateMethod_verifyToken_throws_PasswordRecoveryInvalidToken() {
        assertThrows(PasswordRecoveryInvalidToken.class,
                () -> passwordRecoveryService.validateToken("invalid_token"),
                "private Method verifyToken DOESN'T throw PasswordRecoveryInvalidToken exception!!");
    }

    @Test
    void validateToken_01_expiresTokenDate() {
        assertThrows(PasswordRecoveryTokenExpired.class,
                () -> passwordRecoveryService.validateToken(
                        createJwtToken(1L, new Date(1L), TOKEN_CLAIM, JWT_SECRET)),
                "Token Date expires exception NOT Thrown!!");
    }

    @Test
    void validateToken_02_invalidTokenClaim() {
        assertThrows(PasswordRecoveryTokenExpired.class,
                () -> passwordRecoveryService.validateToken(
                        createJwtToken(1L, new Date(1L), "invalid_token", JWT_SECRET)),
                "Invalid Token Claim exception NOT Thrown!!!");
    }

    @Test
    void validateToken_03_invalidTokenSecret() {
        assertThrows(PasswordRecoveryInvalidToken.class,
                () -> passwordRecoveryService.validateToken(
                        createJwtToken(1L, new Date(1L), TOKEN_CLAIM, "invalid_secret")),
                "invalid Token Secret exception  NOT Thrown");
    }

    @Test
    void validateToken_04_tokenHasNonexistantUserId() {
        assertThrows(PasswordRecoveryInvalidToken.class,
                () -> passwordRecoveryService.validateToken(
                        createJwtToken(-1L, addDaysToToday(1), TOKEN_CLAIM, JWT_SECRET)),
                " token Has Nonexistant UserId exception is NOT FOUND");
    }

    @Test
    void validateToken_05_dbTokenDoesntEqualUserToken() {
        PasswordRecoveryToken dbToken = new PasswordRecoveryToken();
        dbToken.setToken(createJwtToken(1L, addDaysToToday(1), TOKEN_CLAIM, JWT_SECRET));
        mockPasswordRecoveryTokenServiceGetByUserIdWithToken(dbToken);

        assertThrows(PasswordRecoveryInvalidToken.class,
                () -> passwordRecoveryService.validateToken(
                        createJwtToken(2L, addDaysToToday(1), TOKEN_CLAIM, JWT_SECRET)),
                "the token from the database should not equal the user token");
    }

    @Test
    void validateToken_06_dbExpirationDateIsBeforeNow() {
        PasswordRecoveryToken dbToken = new PasswordRecoveryToken();
        dbToken.setToken(createJwtToken(1L, addDaysToToday(1), TOKEN_CLAIM, JWT_SECRET));
        dbToken.setExpirationDate(LocalDateTime.now().minus(10, ChronoUnit.DAYS));
        mockPasswordRecoveryTokenServiceGetByUserIdWithToken(dbToken);

        assertThrows(PasswordRecoveryTokenExpired.class,
                () -> passwordRecoveryService.validateToken(
                        createJwtToken(1L, addDaysToToday(1), TOKEN_CLAIM, JWT_SECRET)),
                "the ExpirationDate of the token from the database should be Before Now");
    }

    @Test
    void validateToken_07_dbTokenIsGood() {
        PasswordRecoveryToken dbToken = new PasswordRecoveryToken();
        String token = createJwtToken(1L, addDaysToToday(1), TOKEN_CLAIM, JWT_SECRET);
        dbToken.setToken(token);
        dbToken.setExpirationDate(LocalDateTime.now().plus(10, ChronoUnit.DAYS));
        mockPasswordRecoveryTokenServiceGetByUserIdWithToken(dbToken);
        try {
            passwordRecoveryService.validateToken(token);
        } catch (Exception e) {
            fail("dbToken should be validated");
        }
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
        Mockito.when(passwordRecoveryTokenService.getByUserId(ArgumentMatchers.anyLong())).thenReturn(token);
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

    Date addDaysToToday(int days) {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.setTime(new Date());
        tomorrow.add(Calendar.DATE, days);
        return tomorrow.getTime();
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