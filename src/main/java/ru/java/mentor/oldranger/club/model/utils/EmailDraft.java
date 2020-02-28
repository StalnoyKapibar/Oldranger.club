package ru.java.mentor.oldranger.club.model.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "email_drafts")
public class EmailDraft {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject")
    private String subject;

    @Column(name = "message", columnDefinition="TEXT")
    private String message;

    @Column(name = "last_edit")
    private LocalDateTime lastEditDate;

}
