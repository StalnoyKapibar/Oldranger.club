package ru.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationUserDto {
    private String nickName;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String key;

    @Override
    public String toString() {
        return nickName + " " + firstName + " "
                + lastName + " " + email + " " + password + " " + key;
    }
}