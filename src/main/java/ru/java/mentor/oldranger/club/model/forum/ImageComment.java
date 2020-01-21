package ru.java.mentor.oldranger.club.model.forum;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.java.mentor.oldranger.club.model.comment.Comment;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "imageComment")
public class ImageComment {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "img", nullable = true)
    private String img;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comment")
    private Comment comment;

}
