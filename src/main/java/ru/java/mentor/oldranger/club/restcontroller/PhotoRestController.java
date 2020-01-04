package ru.java.mentor.oldranger.club.restcontroller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;
import ru.java.mentor.oldranger.club.service.media.PhotoService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/photos")
public class PhotoRestController {

    private PhotoService service;
    private PhotoAlbumService albumService;

    @RequestMapping(value = "/{albumId}", method = RequestMethod.POST)
    public Photo savePhoto(@RequestBody MultipartFile photo, @PathVariable("albumId") String albumId) {
        return service.save(albumService.findById(Long.parseLong(albumId)), photo);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Photo getPhoto(@PathVariable("id") String id) {
        return service.findById(Long.parseLong(id));
    }

    @PutMapping
    public Photo updatePhoto(@RequestBody Photo photo) {
        return service.update(photo);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deletePhoto(@PathVariable("id") String id) {
        service.deletePhoto(Long.parseLong(id));
    }
}
