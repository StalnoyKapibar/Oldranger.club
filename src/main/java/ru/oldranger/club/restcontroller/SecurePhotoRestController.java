package ru.oldranger.club.restcontroller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.oldranger.club.model.media.Photo;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.service.media.PhotoAlbumService;
import ru.oldranger.club.service.media.PhotoService;
import ru.oldranger.club.service.utils.SecurityUtilsService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/securedPhoto")
@Tag(name = "Secured photos")
public class SecurePhotoRestController {


    private final SecurityUtilsService securityUtilsService;
    private final PhotoService photoService;
    private final PhotoAlbumService photoAlbumService;

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
        if (currentUser != null) {
            Photo photo = photoService.findById(photoId);
            if (photo != null) {
                Set<User> viewers = photo.getAlbum().getViewers();
                if (viewers != null) {
                    if (viewers.contains(currentUser) || viewers.size() == 0) {
                        try {
                            return ResponseEntity.ok(IOUtils.toByteArray(new FileInputStream(
                                    new File(albumsdDir + File.separator +
                                            (type == null || type.equals("original") ? photo.getOriginal() : photo.getSmall())))));
                        } catch (NullPointerException | IOException e) {
                            log.error("error in getting image");
                            log.error(e.getMessage());
                        }
                    }
                }
            }
        }
        return ResponseEntity.badRequest().build();
    }
}
