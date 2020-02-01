package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class PasswordRecoveryStatusDto {
    private String recoveryStatus;
    private Object next_recovery_possible;
}
