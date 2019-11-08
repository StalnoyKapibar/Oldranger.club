package ru.java.mentor.oldranger.club.exceptions.passwordrecovery;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PasswordRecoveryIntervalViolation extends Exception {
    private LocalDateTime nextPossibleRecoveryTime;

    public PasswordRecoveryIntervalViolation(LocalDateTime nextPossibleRecoveryTime) {
        this.nextPossibleRecoveryTime = nextPossibleRecoveryTime;
    }

    public Object getNextPossibleRecoveryTime() {
        return null;
    }
}