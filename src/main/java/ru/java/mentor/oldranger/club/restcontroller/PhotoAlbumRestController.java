package ru.java.mentor.oldranger.club.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.dto.PhotoAlbumDto;
import ru.java.mentor.oldranger.club.dto.PhotoWithAlbumDTO;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.model.user.Role;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.user.RoleService;
import ru.java.mentor.oldranger.club.service.user.UserService;
import ru.java.mentor.oldranger.club.service.utils.SecurityUtilsService;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/albums")
@Tag(name = "Photo album")
public class PhotoAlbumRestController {

    private PhotoAlbumService albumService;
    private PhotoService photoService;
    private SecurityUtilsService securityUtilsService;
    private UserService userService;
    private RoleService roleService;


    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all photo albums for current user", tags = {"Photo album"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PhotoAlbumDto.class)))),
            @ApiResponse(responseCode = "400", description = "Login error")})
    @GetMapping
    public ResponseEntity<List<PhotoAlbumDto>> getPhotoAlbums() {
        if (securityUtilsService.getLoggedUser() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(albumService.findPhotoAlbumsDtoOwnedByUser(securityUtilsService.getLoggedUser()));
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all admin photo albums on first page", tags = {"Photo album"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PhotoAlbumDto.class)))),
            @ApiResponse(responseCode = "204", description = "No albums found"),
            @ApiResponse(responseCode = "401", description = "User have no authority")})
    @Parameter(in = ParameterIn.QUERY, name = "page",
            required = false, description = "номер страницы (необязательный параметр), по дефолтному значению равен - 0",
            allowEmptyValue = true,
            schema = @Schema(
                    type = "Integer",
                    example = "http://localhost:8888/api/albums/all?page=1"))
    @Parameter(in = ParameterIn.QUERY, name = "query",
            required = false, description = "значение для фильтрации наименования альбома(необязательный параметр) только для строки в таблице " +
            "'photo_album', где данное значение содержится в колонке title",
            allowEmptyValue = true, allowReserved = true,
            schema = @Schema(
                    type = "string",
                    example = "http://localhost:8888/api/albums/all?query=Album10"))
    @Parameter(in = ParameterIn.QUERY, name = "dateSort",
            required = false, description = "порядок выведения альбомов по дате добавления(необязательный параметр), по дефолному значению(dateSort=false) " +
            "фотоальбомы выводятся сначала новые к более старым. Если dateSort=true, то сначала выводятся альбомы более старые",
            allowEmptyValue = true, allowReserved = true,
            schema = @Schema(
                    type = "boolean",
                    example = "http://localhost:8888/api/albums/all?dateSort=true"))
    @GetMapping(value = "/all", produces = {"application/json"})
    public ResponseEntity<List<PhotoAlbumDto>> getAdminPhotoAlbums(@RequestParam(value = "page", required = false) Integer page,
                                                                   @RequestParam(value = "query", required = false) String query,
                                                                   @RequestParam(value = "dateSort", required = false) boolean dateSort) {
        if (securityUtilsService.getLoggedUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<PhotoAlbumDto> dto;
        if (page == null) page = 0;
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id"));
        Role role = roleService.getRoleByAuthority("ROLE_ADMIN");
        List<PhotoAlbum> photoAlbums = albumService.findPhotoAlbumsByWritersIn(pageable, userService.findUsersByRole(role)).getContent();
        if (query != null && !query.trim().isEmpty()) {
            query = query.toLowerCase().trim();
            dto = albumService.findPhotoAlbumsDtoByQuery(photoAlbums, query, dateSort);
        } else {
            dto = albumService.findPhotoAlbumsDto(photoAlbums, dateSort);
        }
        if(photoAlbums == null || dto == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Save photo album", tags = {"Photo album"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PhotoAlbumDto.class)))),
            @ApiResponse(responseCode = "401", description = "Login or rights error")})
    @PostMapping
    public ResponseEntity<PhotoAlbumDto> savePhotoAlbum(@RequestParam(value = "albumTitle") String albumTitle) {
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null || albumTitle.equals("")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        PhotoAlbum album = new PhotoAlbum(albumTitle);
        album.addWriter(currentUser);
        album.setAllowView(true);
        return ResponseEntity.ok(albumService.assemblePhotoAlbumDto(albumService.save(album)));
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get photo album", tags = {"Photo album"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PhotoAlbum.class)))),
            @ApiResponse(responseCode = "400", description = "Rights or id error"),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @GetMapping("/{id}")
    public ResponseEntity<PhotoAlbumDto> getAlbum(@PathVariable("id") String id) {
        PhotoAlbum photoAlbum = albumService.findById(Long.parseLong(id));
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (photoAlbum == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!photoAlbum.getViewers().contains(currentUser) && !securityUtilsService.isAdmin() &&
                !securityUtilsService.isModerator() && photoAlbum.getViewers().size() != 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(albumService.assemblePhotoAlbumDto(photoAlbum));
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Get all photos from album", tags = {"Photo album"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PhotoWithAlbumDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Id error"),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @GetMapping("/getPhotos/{id}")
    public ResponseEntity<List<PhotoWithAlbumDTO>> getPhotosByAlbum(@PathVariable("id") String id) {
        PhotoAlbum photoAlbum = albumService.findById(Long.parseLong(id));
        User currentUser = securityUtilsService.getLoggedUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (photoAlbum == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!photoAlbum.getViewers().contains(currentUser) && !securityUtilsService.isAdmin() &&
                !securityUtilsService.isModerator() && photoAlbum.getViewers().size() != 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(albumService.getAllPhotoWithAlbumsDTO(photoAlbum));
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Update album", tags = {"Photo album"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PhotoAlbum.class)))),
            @ApiResponse(responseCode = "400", description = "Update error"),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @PutMapping("/{id}")
    public ResponseEntity<PhotoAlbum> updateAlbum(@PathVariable("id") String id,
                                                  @RequestParam(value = "photoId", required = false) String photoId,
                                                  @RequestParam(value = "title", required = false) String title) {
        User currentUser = securityUtilsService.getLoggedUser();
        PhotoAlbum album = albumService.findById(Long.parseLong(id));
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (album == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!album.getWriters().contains(currentUser) && !securityUtilsService.isAdmin() &&
                !securityUtilsService.isModerator() && album.getWriters().size() != 0) {
            return ResponseEntity.badRequest().build();
        }
        if (photoId != null && !photoId.equals("")) {
            Photo photo = photoService.findById(Long.parseLong(photoId));
            if (photo == null) {
                return ResponseEntity.badRequest().build();
            }
            album.setThumbImage(photo);
        }
        if (photoId.equals("") | photoId == null) {
            album.setThumbImage(null);
        }
        if (title != null) {
            if (title.equals("")) {
                return ResponseEntity.badRequest().build();
            }
            album.setTitle(title);
        }
        return ResponseEntity.ok(albumService.update(album));
    }

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Delete album", tags = {"Photo album"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Delete error"),
            @ApiResponse(responseCode = "401", description = "User have not authority")})
    @DeleteMapping("/{id}")
    public ResponseEntity deleteAlbum(@PathVariable("id") String id) {
        User currentUser = securityUtilsService.getLoggedUser();
        PhotoAlbum album = albumService.findById(Long.parseLong(id));
        if (album == null || currentUser == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!album.getWriters().contains(currentUser) && !securityUtilsService.isAdmin() &&
                !securityUtilsService.isModerator() && album.getWriters().size() != 0) {
            return ResponseEntity.badRequest().build();
        }
        List<Photo> photoList = albumService.getAllPhotosByAlbum(album);
        for (Photo photo : photoList) {
            photoService.deletePhoto(photo.getId());
        }
        albumService.deleteAlbum(Long.parseLong(id));
        return ResponseEntity.ok("delete ok");
    }

}
