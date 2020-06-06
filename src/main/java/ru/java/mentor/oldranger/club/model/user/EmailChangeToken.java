package ru.java.mentor.oldranger.club.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "email_change_token")
public class EmailChangeToken {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="key_change_email")
    String key;

    @JoinColumn(name = "user")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "new_mail")
    private String newEmail;

    public EmailChangeToken(String key, User user, String newEmail) {
        this.key = key;
        this.user = user;
        this.newEmail = newEmail;
    }
}

