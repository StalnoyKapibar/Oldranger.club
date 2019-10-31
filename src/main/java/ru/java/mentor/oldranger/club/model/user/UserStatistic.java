package ru.java.mentor.oldranger.club.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "userStatistic")
public class UserStatistic {

    @Id
    @Column(name = "id")
    private Long id;

    private Long messageCount;

    private LocalDateTime lastVizit;

    @OneToOne
    @MapsId
    private User user;
}