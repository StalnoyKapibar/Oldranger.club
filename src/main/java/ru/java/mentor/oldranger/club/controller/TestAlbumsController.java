package ru.java.mentor.oldranger.club.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;

@Controller
public class TestAlbumsController {
    private PhotoAlbumService albumService;

    @Autowired
    public void setInvitationService(PhotoAlbumService service) {
        this.albumService = service;
    }

    @GetMapping("/albums")
    protected String createAlbum() {
        return "albums";
    }
}
