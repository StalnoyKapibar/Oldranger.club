package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestRegistrationDto {

    private String firstName;
    private String lastName;
    private String email;
    private String about;
}
