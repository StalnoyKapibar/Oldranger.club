package ru.java.mentor.oldranger.club.service.user.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryIntervalViolation;
import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryInvalidToken;
import ru.java.mentor.oldranger.club.exceptions.passwordrecovery.PasswordRecoveryTokenExpired;
import ru.java.mentor.oldranger.club.model.user.PasswordRecoveryToken;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.mail.MailService;
import ru.java.mentor.oldranger.club.service.user.PasswordRecoveryService;
import ru.java.mentor.oldranger.club.service.user.PasswordRecoveryTokenService;
import ru.java.mentor.oldranger.club.service.user.UserService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {

    private static final Logger LOG = LoggerFactory.getLogger(PasswordRecoveryServiceImpl.class);

    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordRecoveryTokenService passwordRecoveryTokenService;

    @Value("${server.protocol}")
    private String SERVER_PROTOCOL;

    @Value("${server.name}")
    private String SERVER_HOST;

    @Value("${server.port}")
    private String SERVER_PORT;

    @Value("${project.jwt.secret-word}")
    private String JWT_SECRET;

    @Value("${project.password-recovery.token-expiration}")
    private String PASSWORD_RECOVERY_TOKEN_EXPIRATION_PATTERN;

    @Value("${project.password-recovery.interval}")
    private String PASSWORD_RECOVERY_INTERVAL;

    private final String TOKEN_CLAIM = "userid";

    @Override
    public void sendRecoveryTokenToEmail(User user) throws PasswordRecoveryIntervalViolation {
        LOG.info("Sending recovery token to user with id = {}", user.getId());
        PasswordRecoveryToken token;
        PasswordRecoveryToken tokenPersist = passwordRecoveryTokenService.getByUserId(user.getId());

        if (tokenPersist == null) {
            token = createTokenForUser(user);
            passwordRecoveryTokenService.save(token);
        } else {
            LocalDateTime nextPossibleRecoveryTime = calculateNextPossibleRecoveryTime(tokenPersist.getIssueDate());
            LocalDateTime now = LocalDateTime.now();
            if (nextPossibleRecoveryTime.isBefore(now)) {
                updateToken(tokenPersist);
                passwordRecoveryTokenService.save(tokenPersist);
                token = tokenPersist;
            } else {
                throw new PasswordRecoveryIntervalViolation(nextPossibleRecoveryTime);
            }
        }
        composeAndSendTokenEmail(token);
    }

    @Override
    public PasswordRecoveryToken validateToken(String recoveryToken) throws PasswordRecoveryInvalidToken, PasswordRecoveryTokenExpired {
        LOG.debug("Validating token {}", recoveryToken);
        verifyToken(recoveryToken);
        Long userId = getUserIdFromToken(recoveryToken);
        return verifyTokenPersistCompare(recoveryToken, userId);
    }

    @Override
    public void updatePassword(PasswordRecoveryToken recoveryToken, String password) {
        LOG.info("Updating password");
        User user = recoveryToken.getUser();
        user.setPassword(passwordEncoder.encode(password));
        userService.save(user);
        passwordRecoveryTokenService.delete(recoveryToken);
        LOG.info("Password updated");
    }


    private void composeAndSendTokenEmail(PasswordRecoveryToken token) {
        LOG.info("Composing and sending email");
        String msgSubject = "Восстановление пароля";
        String recoverURL = getFullHostName() + "/passwordrecovery/token/" + token.getToken();
        mailService.send(token.getUser().getEmail(), msgSubject, recoverURL);
    }

    private PasswordRecoveryToken verifyTokenPersistCompare(String token, long userId) throws PasswordRecoveryInvalidToken, PasswordRecoveryTokenExpired {
        LOG.debug("Verifying token");
        PasswordRecoveryToken dbToken = passwordRecoveryTokenService.getByUserId(userId);
        LocalDateTime now = LocalDateTime.now();
        if (dbToken == null) {
            throw new PasswordRecoveryInvalidToken();
        }
        if (!(dbToken.getToken().equals(token))) {
            throw new PasswordRecoveryInvalidToken();
        }
        if (dbToken.getExpirationDate().isBefore(now)) {
            throw new PasswordRecoveryTokenExpired();
        }
        return dbToken;
    }

    private void verifyToken(String token) throws PasswordRecoveryInvalidToken, PasswordRecoveryTokenExpired {
        LOG.debug("Verifying token {}", token);
        try {
            JWT.require(Algorithm.HMAC512(JWT_SECRET))
                    .build()
                    .verify(token);
        } catch (TokenExpiredException e) {
            LOG.error(e.getMessage(), e);
            throw new PasswordRecoveryTokenExpired();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new PasswordRecoveryInvalidToken();
        }
    }

    private Long getUserIdFromToken(String token) throws PasswordRecoveryInvalidToken {
        LOG.debug("Getting user id from token {}", token);
        DecodedJWT decodedToken;
        try {
            decodedToken = JWT.decode(token);
        } catch (JWTDecodeException e) {
            LOG.error(e.getMessage(), e);
            throw new PasswordRecoveryInvalidToken();
        }
        Long userId = decodedToken.getClaim(TOKEN_CLAIM).asLong();
        if (userId == null) {
            LOG.error("Invalid token");
            throw new PasswordRecoveryInvalidToken();
        }
        LOG.debug("Returned id {}", userId);
        return userId;
    }

    private String getFullHostName() {
        //TODO construct host name for production in another way
        return SERVER_PROTOCOL + "://" + SERVER_HOST + ":" + SERVER_PORT;
    }

    private PasswordRecoveryToken createTokenForUser(User user) {
        LOG.debug("Creating token for user with id = {}", user.getId());
        LocalDateTime issueTime = LocalDateTime.now();
        LocalDateTime expirationTime = calculateExpirationTime(issueTime);
        String jwtToken = createJwtToken(user.getId(), localDateTimeToDate(expirationTime));
        return new PasswordRecoveryToken(user, issueTime, expirationTime, jwtToken);
    }

    private void updateToken(PasswordRecoveryToken token) {
        LOG.debug("Updating token");
        LocalDateTime issueTime = LocalDateTime.now();
        LocalDateTime expirationTime = calculateExpirationTime(issueTime);
        String jwtToken = createJwtToken(token.getUser().getId(), localDateTimeToDate(expirationTime));

        token.setIssueDate(issueTime);
        token.setExpirationDate(expirationTime);
        token.setToken(jwtToken);
    }

    private String createJwtToken(long userId, Date expirationTime) {
        LOG.info("Creating jwt token");
        String token = null;
        try {
            token = JWT
                    .create()
                    .withClaim(TOKEN_CLAIM, userId)
                    .withExpiresAt(expirationTime)
                    .sign(Algorithm.HMAC512(JWT_SECRET));
            LOG.info("Created token {}", token);
        } catch (JWTDecodeException e) {
            LOG.error(e.getMessage(), e);
        }
        return token;
    }

    private LocalDateTime calculateExpirationTime(LocalDateTime startTime) {
        String[] DHM = PASSWORD_RECOVERY_TOKEN_EXPIRATION_PATTERN.split("-");
        return calculateOffsetPatternDHM(startTime, DHM);
    }

    private LocalDateTime calculateNextPossibleRecoveryTime(LocalDateTime lastIssueTime) {
        String[] DHM = PASSWORD_RECOVERY_INTERVAL.split("-");
        return calculateOffsetPatternDHM(lastIssueTime, DHM);
    }

    private LocalDateTime calculateOffsetPatternDHM(LocalDateTime localDateTime, String[] DHM) {
        long daysOffset = Long.parseLong(DHM[0]);
        long hoursOffset = Long.parseLong(DHM[1]);
        long minutesOffset = Long.parseLong(DHM[2]);
        return localDateTime
                .plusDays(daysOffset)
                .plusHours(hoursOffset)
                .plusMinutes(minutesOffset);
    }

    private Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}