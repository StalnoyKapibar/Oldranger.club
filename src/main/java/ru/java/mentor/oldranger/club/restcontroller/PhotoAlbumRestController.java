package ru.java.mentor.oldranger.club.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.java.mentor.oldranger.club.model.user.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class PhotoAlbumRestController {

    private PhotoAlbumService albumService;

    @Autowired
    public void setPhotoAlbumService(PhotoAlbumService service) {
        this.albumService = service;
    }

    @GetMapping
    public List<PhotoAlbum> getPhotoAlbums() {
        return albumService.findAll();
    }

    @PostMapping
    public PhotoAlbum savePhotoAlbum(@RequestBody PhotoAlbum album) {
        return albumService.save(album);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public PhotoAlbum getAlbum(@PathVariable("id") String id) {
        return albumService.findById(Long.parseLong(id));
    }

    @PutMapping
    public PhotoAlbum updateAlbum(@RequestBody PhotoAlbum album) {
        return albumService.update(album);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteAlbum(@PathVariable("id") String id) {
        albumService.deleteAlbum(id);
    }
}
