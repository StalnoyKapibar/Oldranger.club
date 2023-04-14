package ru.oldranger.club.service.utils;

import ru.oldranger.club.exceptions.passwords.PasswordIllegal;
import ru.oldranger.club.exceptions.passwords.PasswordTooLong;
import ru.oldranger.club.exceptions.passwords.PasswordTooShort;

public interface PasswordsService {
    void checkStrength(String password) throws PasswordTooShort, PasswordTooLong, PasswordIllegal;
}