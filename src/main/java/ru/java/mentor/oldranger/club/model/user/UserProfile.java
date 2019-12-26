package ru.java.mentor.oldranger.club.model.user;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "userProfile")
public class UserProfile {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "birthday")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "social_vk")
    private String socialVk;

    @Column(name = "social_fb")
    private String socialFb;

    @Column(name = "social_tw")
    private String socialTw;

    @Column(name = "about_me")
    private String aboutMe;

    public UserProfile(User user) {
        this.user = user;
    }

    public void setField(String fieldName, String fieldValue) throws NoSuchFieldException, IllegalAccessException {
        if (!fieldValue.isEmpty()) {
            Field field = this.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(this, fieldValue);
        }
    }
}