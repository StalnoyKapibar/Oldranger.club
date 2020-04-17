package ru.java.mentor.oldranger.club.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Objects;

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

    @Column(name = "nameRole")
    private String nameRole;


    public Role(String role) {
        this.role = role;
    }

    public Role(String role, String nameRole){
        this.role = role;
        this.nameRole = nameRole;
    }

    @Override
    public String getAuthority() {
        return getRole();
    }

    public String getRole() {
        return role;
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