package ru.oldranger.club.model.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
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