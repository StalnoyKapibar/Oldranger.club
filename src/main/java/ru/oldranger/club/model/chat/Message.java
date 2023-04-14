package ru.oldranger.club.model.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "msg_text")
    private String text;

    @Column(name = "img_original")
    private String originalImg;

    @Column(name = "img_thumbnail")
    private String thumbnailImg;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private MessageType type;

    public enum MessageType {
        MESSAGE,
        JOIN,
        LEAVE
    }

    @Column(name = "sender")
    private String sender;

    @Column(name = "sender_ava")
    private String senderAvatar;

    @Column(name = "replyTo")
    private String replyTo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chat")
    private Chat chat;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMMM HH:mm", locale = "RU")
    @Column(name = "message_date")
    private LocalDateTime messageDate;

    @Column(name = "edit_message_date")
    private LocalDateTime editMessageDate;
}