package ru.java.mentor.oldranger.club.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoleDto {
    private Long id;
    private String nameRole;
    private String role;
}
