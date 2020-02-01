package ru.java.mentor.oldranger.club.model.user;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requestInvitations")
public class RequestInvitation {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "about_Me", nullable = false, length = 8000)
    private String aboutMe;
}
