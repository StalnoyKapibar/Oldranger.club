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
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.service.media.PhotoPositionService;
import ru.java.mentor.oldranger.club.service.media.PhotoService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/photos")
@Tag(name = "Position photo")
public class PositionPhotoOnAlbumRestController {

    private final PhotoPositionService photoPositionService;

    @Operation(security = @SecurityRequirement(name = "security"),
            summary = "Position photo in album", tags = {"Photo"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Photo.class)))),
            @ApiResponse(responseCode = "400", description = "Rights error")})
    @RequestMapping(value = "position/{photoId}", method = RequestMethod.POST)
//    public ResponseEntity<String> positionPhotoOnAlbum(@PathVariable("photoId") long photoId, @RequestParam("position") long position, @RequestParam("albumId") long albumId) {
    public ResponseEntity<String> positionPhotoOnAlbum(List<long>) {
        photoPositionService.setPositionPhotoOnAlbum(list, albumId);
        return ResponseEntity.ok("change of position");
    }
}
