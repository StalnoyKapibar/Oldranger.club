package ru.java.mentor.oldranger.club.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "avatars")
public class UserAvatar {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_img", nullable = true)
    private String original;

    @Column(name = "medium_img", nullable = true)
    private String medium;

    @Column(name = "small_img", nullable = true)
    private String small;

}