package ru.java.mentor.oldranger.club.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.java.mentor.oldranger.club.model.user.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;

@RestController
@RequestMapping("/api/albums")
public class PhotoAlbumRestController {

    private PhotoAlbumService albumService;

    @Autowired
    public void setInvitationService(PhotoAlbumService service) {
        this.albumService = service;
    }

    @PostMapping
    protected void savePhotoAlbum(@RequestBody String title) {
        PhotoAlbum album = albumService.createAlbum(title);
        albumService.save(album);
    }
}
