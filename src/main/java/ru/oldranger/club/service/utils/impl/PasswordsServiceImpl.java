package ru.oldranger.club.service.utils.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.oldranger.club.exceptions.passwords.PasswordIllegal;
import ru.oldranger.club.exceptions.passwords.PasswordTooLong;
import ru.oldranger.club.exceptions.passwords.PasswordTooShort;
import ru.oldranger.club.service.utils.PasswordsService;

@Service
public class PasswordsServiceImpl implements PasswordsService {

    @Value("${project.password.min-length}")
    private int MIN_LENGTH;

    private final int MAX_LENGTH = 255;

    @Override
    public void checkStrength(String password) throws PasswordTooShort, PasswordTooLong, PasswordIllegal {
        if (password == null || password.length() < MIN_LENGTH) {
            throw new PasswordTooShort();
        }
        if (password.length() > MAX_LENGTH) {
            throw new PasswordTooLong();
        }
    }
}