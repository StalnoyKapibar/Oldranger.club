package ru.java.mentor.oldranger.club.service.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.java.mentor.oldranger.club.model.user.PasswordRecoveryToken;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.mail.MailService;
import ru.java.mentor.oldranger.club.service.user.PasswordRecoveryService;
import ru.java.mentor.oldranger.club.service.user.PasswordRecoveryTokenService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
class PasswordRecoveryServiceImplTest {

    @Autowired
    private PasswordRecoveryServiceImpl passwordRecoveryService;

    @MockBean
    private PasswordRecoveryTokenServiceImpl passwordRecoveryTokenService;

    @MockBean
    private MailService mailService;

    @Test
    void sendRecoveryTokenToEmail_whenTokenDoesntExist() {
        User user = new User();
        user.setId(1L);

        PasswordRecoveryToken token = new PasswordRecoveryToken();

        Mockito.when(passwordRecoveryTokenService.getByUserId(user.getId())).thenReturn(null);
        doNothing().when(passwordRecoveryTokenService).save(isA(PasswordRecoveryToken.class));
        doNothing().when(mailService).send(isA(String.class), isA(String.class), isA(String.class));
        try {
            passwordRecoveryService.sendRecoveryTokenToEmail(user);
        } catch (Exception e) {
            log.info("test PasswordRecoveryServiceImplTest sendRecoveryTokenToEmail_whenTokenDoesntExist");
        }
        Mockito.verify(passwordRecoveryTokenService, Mockito.times(1)).save(isA(PasswordRecoveryToken.class));
        Mockito.verify(mailService, Mockito.times(1)).send(isA(String.class), isA(String.class), isA(String.class));
    }
}