package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordRecoveryDto {

    private String token;
    private String tokenStatus;
    private String passwordStatus;
}
