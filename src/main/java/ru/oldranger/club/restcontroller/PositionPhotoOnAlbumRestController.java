package ru.oldranger.club.restcontroller;

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
import ru.oldranger.club.dto.PhotosDto;
import ru.oldranger.club.model.media.Photo;
import ru.oldranger.club.service.media.PhotoPositionService;

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
    @RequestMapping(value = "position/{albumId}", method = RequestMethod.POST)
    public ResponseEntity<String> positionPhotoOnAlbum(@RequestBody PhotosDto photosDto, @PathVariable long albumId) {
        photoPositionService.setPositionPhotoOnAlbumWithIdPhoto(photosDto.getPositions(), albumId);
        return ResponseEntity.ok("change of position");
    }
}
