package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class PasswordRecoveryDto {

    private String token;
    private String tokenStatus;
    private String passwordStatus;
}
