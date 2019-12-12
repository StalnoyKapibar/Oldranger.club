package ru.java.mentor.oldranger.club.model.mail;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Column
    LocalDateTime lastSendTime;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public static DirectionType stringToDirectionType(String userDirection) {
        switch (userDirection){
            case "ONE_TO_DAY": return DirectionType.ONE_TO_DAY;
            case "TWO_TO_DAY": return DirectionType.TWO_TO_DAY;
            case "ONE_TO_WEEK": return DirectionType.ONE_TO_WEEK;
            default: return DirectionType.NEVER;
        }
    }



}
