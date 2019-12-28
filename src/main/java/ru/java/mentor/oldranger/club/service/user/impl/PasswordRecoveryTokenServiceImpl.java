package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.java.mentor.oldranger.club.dao.UserRepository.PasswordRecoveryTokenRepository;
import ru.java.mentor.oldranger.club.model.user.PasswordRecoveryToken;
import ru.java.mentor.oldranger.club.service.user.PasswordRecoveryTokenService;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PasswordRecoveryTokenServiceImpl implements PasswordRecoveryTokenService {


    private PasswordRecoveryTokenRepository passwordRecoveryTokenRepository;
    private static final Logger LOG = LoggerFactory.getLogger(PasswordRecoveryTokenServiceImpl.class);

    @Override
    public PasswordRecoveryToken getByUserId(long userId) {
        LOG.debug("Getting PasswordRecoveryToken for user with id = {}", userId);
        PasswordRecoveryToken token = null;
        try {
            token = passwordRecoveryTokenRepository.getByUserId(userId);
            LOG.info("Token returned");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return token;
    }

    @Override
    @Transactional
    public void saveOrUpdateIfExist(PasswordRecoveryToken recoveryToken) {
        LOG.info("Saving PasswordRecoveryToken {}", recoveryToken);
        try {
            passwordRecoveryTokenRepository.saveOrUpdateIfExist(recoveryToken.getUser().getId(), recoveryToken.getToken(), recoveryToken.getIssueDate());
            LOG.info("PasswordRecoveryToken saved");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void delete(PasswordRecoveryToken recoveryToken) {
        LOG.info("Deleting PasswordRecoveryToken {}", recoveryToken);
        try {
            passwordRecoveryTokenRepository.delete(recoveryToken);
            LOG.info("PasswordRecoveryToken deleted");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void save(PasswordRecoveryToken recoveryToken) {
        LOG.info("Saving PasswordRecoveryToken {}", recoveryToken);
        try {
            passwordRecoveryTokenRepository.save(recoveryToken);
            LOG.info("PasswordRecoveryToken saved");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    @Scheduled(cron = "${project.password-recovery.cleanup-db-cron}")
    public void cleanup() {
        LOG.info("Starting token cleanup job");
        try {
            passwordRecoveryTokenRepository.cleanup(LocalDateTime.now());
            LOG.info("Tokens successfully removed");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}