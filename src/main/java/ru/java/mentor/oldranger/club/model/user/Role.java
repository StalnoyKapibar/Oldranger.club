package ru.java.mentor.oldranger.club.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    public final static Role ROLE_ADMIN = new Role("ROLE_ADMIN");
    public final static Role ROLE_USER = new Role("ROLE_USER");
    public final static Role ROLE_MODERATOR = new Role("ROLE_MODERATOR");
    public final static Role ROLE_PROSPECT = new Role("ROLE_PROSPECT");

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role")
    private String role;

    public Role(String role) {
        this.role = role;
    }

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