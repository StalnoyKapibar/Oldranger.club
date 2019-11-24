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
    public void setInvitationService(PhotoAlbumService service) {
        this.albumService = service;
    }

    @GetMapping
    public List<PhotoAlbum> getPhotoAlbums() {
        List<PhotoAlbum> albums = albumService.findAll();
        return albumService.findAll();
    }

    @PostMapping
    public PhotoAlbum savePhotoAlbum(@RequestBody String title) {
        PhotoAlbum album = albumService.save(new PhotoAlbum(title));
        albumService.createAlbum(album.getId() + "");   //TODO перенести в обычный контроллер?
        return album;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public PhotoAlbum getAlbum(@PathVariable("id") String id) {
        return albumService.findById(Long.parseLong(id));
    }

}
