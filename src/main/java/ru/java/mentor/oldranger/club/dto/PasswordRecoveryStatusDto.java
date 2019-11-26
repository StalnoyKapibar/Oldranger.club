package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PasswordRecoveryStatusDto {
    private String recoveryStatus;
    private Object next_recovery_possible;
}
