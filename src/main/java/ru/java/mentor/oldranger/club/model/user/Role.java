package ru.java.mentor.oldranger.club.model.user;

import org.springframework.security.core.GrantedAuthority;

public class Role implements GrantedAuthority {


    @Override
    public String getAuthority() {
        return null;
    }
}
