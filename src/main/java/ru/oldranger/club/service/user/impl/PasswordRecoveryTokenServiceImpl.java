package ru.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.oldranger.club.dao.UserRepository.PasswordRecoveryTokenRepository;
import ru.oldranger.club.model.user.PasswordRecoveryToken;
import ru.oldranger.club.service.user.PasswordRecoveryTokenService;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class PasswordRecoveryTokenServiceImpl implements PasswordRecoveryTokenService {


    private PasswordRecoveryTokenRepository passwordRecoveryTokenRepository;

    @Override
    public PasswordRecoveryToken getByUserId(long userId) {
        log.debug("Getting PasswordRecoveryToken for user with id = {}", userId);
        PasswordRecoveryToken token = null;
        try {
            token = passwordRecoveryTokenRepository.getByUserId(userId);
            log.info("Token returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return token;
    }

    @Override
    @Transactional
    public void saveOrUpdateIfExist(PasswordRecoveryToken recoveryToken) {
        log.info("Saving PasswordRecoveryToken {}", recoveryToken);
        try {
            passwordRecoveryTokenRepository.saveOrUpdateIfExist(recoveryToken.getUser().getId(), recoveryToken.getToken(), recoveryToken.getIssueDate());
            log.info("PasswordRecoveryToken saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void delete(PasswordRecoveryToken recoveryToken) {
        log.info("Deleting PasswordRecoveryToken {}", recoveryToken);
        try {
            passwordRecoveryTokenRepository.delete(recoveryToken);
            log.info("PasswordRecoveryToken deleted");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void save(PasswordRecoveryToken recoveryToken) {
        log.info("Saving PasswordRecoveryToken {}", recoveryToken);
        try {
            passwordRecoveryTokenRepository.save(recoveryToken);
            log.info("PasswordRecoveryToken saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    @Scheduled(cron = "${project.password-recovery.cleanup-db-cron}")
    public void cleanup() {
        log.info("Starting token cleanup job");
        try {
            passwordRecoveryTokenRepository.cleanup(LocalDateTime.now());
            log.info("Tokens successfully removed");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}