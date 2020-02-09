package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.PhotoAlbumDto;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/albums")
@Tag(name = "Photo album")
public class PhotoAlbumRestController {

    private PhotoAlbumService service;
    private PhotoService photoService;
    private SecurityUtilsService securityUtilsService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all photo albums", tags = {"Photo album"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PhotoAlbum.class)))),
            @ApiResponse(responseCode = "400", description = "Login error")})
    @GetMapping
    public ResponseEntity<List<PhotoAlbumDto>> getPhotoAlbums() {
        if (securityUtilsService.getLoggedUser() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.findPhotoAlbumsByUser(securityUtilsService.getLoggedUser()));
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Save photo album", tags = {"Photo album"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PhotoAlbum.class)))),
            @ApiResponse(responseCode = "400", description = "Login or rights error")})
    @PostMapping
    public ResponseEntity<PhotoAlbum> savePhotoAlbum(@RequestParam(value = "albumTitle") String albumTitle) {
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null || albumTitle.equals("")) {
            return ResponseEntity.badRequest().build();
        }
        PhotoAlbum album = new PhotoAlbum(albumTitle);
        album.addWriter(currentUser);
        album.setAllowView(true);
        return ResponseEntity.ok(service.save(album));
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get photo album", tags = {"Photo album"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PhotoAlbum.class)))),
            @ApiResponse(responseCode = "400", description = "Rights or id error")})
    @GetMapping("/{id}")
    public ResponseEntity<PhotoAlbum> getAlbum(@PathVariable("id") String id) {
        PhotoAlbum photoAlbum = service.findById(Long.parseLong(id));
        User currentUser = securityUtilsService.getLoggedUser();
        if (photoAlbum == null || currentUser == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!photoAlbum.getViewers().contains(currentUser) && !securityUtilsService.isAdmin() &&
                !securityUtilsService.isModerator() && photoAlbum.getViewers().size() != 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(photoAlbum);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all photos from album", tags = {"Photo album"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Photo.class)))),
            @ApiResponse(responseCode = "400", description = "Rights or id error")})
    @GetMapping("/getPhotos/{id}")
    public ResponseEntity<List<Photo>> getPhotosByAlbum(@PathVariable("id") String id) {
        PhotoAlbum photoAlbum = service.findById(Long.parseLong(id));
        User currentUser = securityUtilsService.getLoggedUser();
        if (photoAlbum == null || currentUser == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!photoAlbum.getViewers().contains(currentUser) && !securityUtilsService.isAdmin() ||
                !securityUtilsService.isModerator() && photoAlbum.getViewers().size() != 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.getAllPhotos(photoAlbum));
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Update album", tags = {"Photo album"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PhotoAlbum.class)))),
            @ApiResponse(responseCode = "400", description = "Rights error")})
    @PutMapping("/{id}")
    public ResponseEntity<PhotoAlbum> updateAlbum(@PathVariable("id") String id,
                                                  @RequestParam(value = "photoId", required = false) String photoId,
                                                  @RequestParam(value = "title", required = false) String title) {
        User currentUser = securityUtilsService.getLoggedUser();
        PhotoAlbum album = service.findById(Long.parseLong(id));
        if (album == null || currentUser == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!album.getWriters().contains(currentUser) && !securityUtilsService.isAdmin() ||
                !securityUtilsService.isModerator() && album.getWriters().size() != 0) {
            return ResponseEntity.badRequest().build();
        }
        if (photoId != null) {
            Photo photo = photoService.findById(Long.parseLong(photoId));
            if (photo == null) {
                return ResponseEntity.badRequest().build();
            }
            album.setThumbImage(photo);
        }
        if (title != null) {
            album.setTitle(title);
        }
        return ResponseEntity.ok(service.update(album));
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete album", tags = {"Photo album"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Rights error")})
    @DeleteMapping("/{id}")
    public ResponseEntity deleteAlbum(@PathVariable("id") String id) {
        User currentUser = securityUtilsService.getLoggedUser();
        PhotoAlbum album = service.findById(Long.parseLong(id));
        if (album == null || currentUser == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!album.getWriters().contains(currentUser) && !securityUtilsService.isAdmin() ||
                !securityUtilsService.isModerator() && album.getWriters().size() != 0) {
            return ResponseEntity.badRequest().build();
        }
        service.deleteAlbum(Long.parseLong(id));
        return ResponseEntity.ok("delete ok");
    }

}
