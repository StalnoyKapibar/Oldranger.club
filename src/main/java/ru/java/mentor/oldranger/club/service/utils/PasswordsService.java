package ru.java.mentor.oldranger.club.service.utils;

import ru.java.mentor.oldranger.club.exceptions.passwords.PasswordIllegal;
import ru.java.mentor.oldranger.club.exceptions.passwords.PasswordTooLong;
import ru.java.mentor.oldranger.club.exceptions.passwords.PasswordTooShort;

public interface PasswordsService {
    void checkStrength(String password) throws PasswordTooShort, PasswordTooLong, PasswordIllegal;
}
