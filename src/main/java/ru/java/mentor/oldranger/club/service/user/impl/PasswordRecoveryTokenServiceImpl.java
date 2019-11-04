package ru.java.mentor.oldranger.club.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.java.mentor.oldranger.club.dao.UserRepository.PasswordRecoveryTokenRepository;
import ru.java.mentor.oldranger.club.model.user.PasswordRecoveryToken;
import ru.java.mentor.oldranger.club.service.user.PasswordRecoveryTokenService;

@Service
public class PasswordRecoveryTokenServiceImpl implements PasswordRecoveryTokenService {

    @Autowired
    private PasswordRecoveryTokenRepository passwordRecoveryTokenRepository;

    @Override
    public PasswordRecoveryToken getByUserId(long userId) {
        return passwordRecoveryTokenRepository.getByUserId(userId);
    }

    @Transactional
    @Override
    public void saveOrUpdateIfExist(PasswordRecoveryToken recoveryToken) {
        passwordRecoveryTokenRepository.saveOrUpdateIfExist(recoveryToken.getUser().getId(), recoveryToken.getToken(), recoveryToken.getIssueDate());
    }

    @Override
    public void delete(PasswordRecoveryToken recoveryToken) {
        passwordRecoveryTokenRepository.delete(recoveryToken);
    }
}
