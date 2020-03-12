package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/securedPhoto")
@Tag(name = "Secured photos")
public class SecurePhotoRestController {

    private final SecurityUtilsService securityUtilsService;
    private final PhotoService photoService;

    @Value("${photoalbums.location}")
    private String albumsdDir;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Return photo as byte array from album", tags = {"Topic and comments"})
    @Parameter(in = ParameterIn.QUERY, name = "type",
            required = false, description = "размер картинки (необязательный параметр)",
            allowEmptyValue = false,
            schema = @Schema(
                    type = "String",
                    example = "http://localhost:8888/api/securedPhoto/photoFromAlbum/1?type=small"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo found",
                    content = @Content(schema = @Schema(implementation = Array.class))),
            @ApiResponse(responseCode = "400", description = "Secure or id error")})
    @GetMapping(value = "/photoFromAlbum/{photoId}")
    public ResponseEntity<byte[]> getAlbumPhoto(@PathVariable(value = "photoId") Long photoId,
                                                @RequestParam(value = "type", required = false) String type) {
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }
        Photo photo = photoService.findById(photoId);
        if (photo != null) {
            Set<User> viewers = photo.getAlbum().getViewers();
            if (viewers.contains(currentUser) || viewers.isEmpty()) {
                try {
                    CacheControl cache = CacheControl.maxAge(7, TimeUnit.DAYS);
                    return ResponseEntity.ok().cacheControl(cache).body(IOUtils.toByteArray(new FileInputStream(
                            new File(albumsdDir + File.separator +
                                    (type == null || type.equals("original") ? photo.getOriginal() : photo.getSmall())))));
                } catch (NullPointerException | IOException e) {
                    log.error("error in getting image");
                    log.error(e.getMessage());
                }
            }
        }
        return ResponseEntity.badRequest().build();
    }
}