package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordRecoveryStatusDto {
    private String recoveryStatus;
    private Object next_recovery_possible;
}
