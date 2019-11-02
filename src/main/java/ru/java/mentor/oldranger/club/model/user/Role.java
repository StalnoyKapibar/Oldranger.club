package ru.java.mentor.oldranger.club.model.user;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    public Role(String role) {
        this.role = role;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role")
    private String role;

    public String getRole() {
        return role;
    }

    @Override
    public String getAuthority() {
        return getRole();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GrantedAuthority)) return false;
        GrantedAuthority role1 = (GrantedAuthority) o;
        return getAuthority().equals(role1.getAuthority());
    }

    @Override
    public int hashCode() {
        return Objects.hash(role);
    }
}