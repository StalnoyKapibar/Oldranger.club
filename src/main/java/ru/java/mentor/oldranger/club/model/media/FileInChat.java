package ru.java.mentor.oldranger.club.model.media;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "chatFiles")
public class FileInChat {
    @Id
    @Column(name = "id_file")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fileName")
    private String fileName;

    @Column(name = "fileDownloadUri")
    private String location;

    @Column(name = "fileType")
    private String fileType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMMM HH:mm", locale = "RU")
    @Column(name = "upload_file_date")
    private LocalDateTime uploadFileDate;

    @Column(name = "id_chat")
    private Long chatID;
}
