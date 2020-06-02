package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class UserAuthDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String nickName;
    private String role;
    private LocalDateTime currentlyVisit;
    private boolean currentUser;
    private List<String> mute;

}
