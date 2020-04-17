package ru.java.mentor.oldranger.club.exceptions;

import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.java.mentor.oldranger.club.dto.ErrorDto;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorDto> maxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        //return ResponseEntity.ok(new ErrorDto("File size must not exceed 20Mb"));
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(new ErrorDto("File size must not exceed 20Mb"));
    }
}
