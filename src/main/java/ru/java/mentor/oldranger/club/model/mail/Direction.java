package ru.java.mentor.oldranger.club.model.mail;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "userDirection")
public class Direction {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "periodicity")
    @Enumerated(EnumType.STRING)
    private DirectionType directionType;

    @OneToOne(cascade={CascadeType.PERSIST})
    @JoinColumn(name="user_id")
    private User user;

}
