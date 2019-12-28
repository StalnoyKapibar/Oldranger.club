package ru.java.mentor.oldranger.club.model.user;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role")
    private String role;

    public Role(String role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return getRole();
    }
}