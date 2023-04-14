package ru.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecoveryTokenDto {

    private String token;
    private String tokenStatus;
}
