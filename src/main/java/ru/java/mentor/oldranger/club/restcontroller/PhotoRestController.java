package ru.java.mentor.oldranger.club.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.service.media.PhotoService;

@RestController
@RequestMapping("/api/photos")
public class PhotoRestController {

    private PhotoService service;

    @Autowired
    public void setPhotoService(PhotoService service) {
        this.service = service;
    }

    @PostMapping
    public Photo savePhoto(@RequestBody Photo photo) {
        return service.save(photo);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Photo getPhoto(@PathVariable("id") String id) {
        return service.findById(Long.parseLong(id));
    }

    @PutMapping
    public Photo updatePhoto(@RequestBody Photo photo) {
        return service.save(photo);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deletePhoto(@PathVariable("id") String id) {
        service.deletePhoto(Long.parseLong(id));
    }
}
