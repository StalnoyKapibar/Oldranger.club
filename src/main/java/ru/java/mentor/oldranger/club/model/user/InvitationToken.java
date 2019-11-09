package ru.java.mentor.oldranger.club.model.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "invitation_token")
public class InvitationToken {
    @Id
    @Column(name = "id_invite")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key_invite")
    private String key;

    @Column(name = "date_invite")
    private Date date;

    @JoinColumn(name = "id_user")
    @ManyToOne
    private User user;

    @Column(name = "used_invite")
    private Boolean used;

    public InvitationToken() {
    }

    public InvitationToken(String key, User user) {
        this.key = key;
        this.user = user;
        this.date = new Date();
        used = false;
    }
}
