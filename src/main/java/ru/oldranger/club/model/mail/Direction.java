package ru.oldranger.club.model.mail;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.oldranger.club.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "directions")
public class Direction {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "periodicity")
    @Enumerated(EnumType.STRING)
    private DirectionType directionType;

    @Column
    private LocalDateTime lastSendTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static DirectionType stringToDirectionType(String userDirection) {
        switch (userDirection) {
            case "ONE_TO_DAY":
                return DirectionType.ONE_TO_DAY;
            case "TWO_TO_DAY":
                return DirectionType.TWO_TO_DAY;
            case "ONE_TO_WEEK":
                return DirectionType.ONE_TO_WEEK;
            default:
                return DirectionType.NEVER;
        }
    }

}
