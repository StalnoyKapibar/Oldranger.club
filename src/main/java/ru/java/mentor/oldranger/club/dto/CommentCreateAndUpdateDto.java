package ru.java.mentor.oldranger.club.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "This is CommentCreateAndUpdateDto,  where answerID - ID of comment on which answer was given ",
        requiredProperties = {"idUser", "idTopic"})
public class CommentCreateAndUpdateDto {

    private Long idTopic;
    private Long  idUser ;
    @NotEmpty
    @Pattern(regexp = "^(?=\\s*\\S).*$")
    private String text;
    private Long answerID;

    @Override
    public String toString() {
        return "CommentCreateAndUpdateDto{" +
                "idTopic=" + idTopic +
                ", text='" + text + '\'' +
                ", answerID='" + answerID + '\'' +
                '}';
    }
}