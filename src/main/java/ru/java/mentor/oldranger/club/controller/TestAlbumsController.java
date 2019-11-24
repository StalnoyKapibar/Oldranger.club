package ru.java.mentor.oldranger.club.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import ru.java.mentor.oldranger.club.model.user.media.Photo;
import ru.java.mentor.oldranger.club.model.user.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;
import ru.java.mentor.oldranger.club.service.media.PhotoService;

import java.util.List;

@Controller
public class TestAlbumsController {
    private PhotoAlbumService albumService;
    private PhotoService photoService;
    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setInvitationService(PhotoAlbumService service) {
        this.albumService = service;
    }

    @Autowired
    protected void setPhotoService(PhotoService service) {
        this.photoService = service;
    }

    @Value("${server.protocol}")
    private String protocol;

    @Value("${server.name}")
    private String host;

    @Value("${server.port}")
    private String port;

    @GetMapping("/albums")
    protected String createAlbum() {
        return "albums";
    }

    @RequestMapping(value = "album/{id}", method = RequestMethod.GET)
    public ModelAndView getAlbum(@PathVariable("id") String id) {
        PhotoAlbum album = restTemplate.getForObject(protocol + "://" + host + ":" + port + "/api/albums/" + id, PhotoAlbum.class);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("album");
        modelAndView.addObject("album", album);
        return modelAndView;
    }


    @RequestMapping(value = "album/uploadPhoto", method = RequestMethod.POST)
    public ModelAndView uploadPhoto(@RequestParam("file") MultipartFile file, String albumId) {
        PhotoAlbum album = albumService.findById(Long.parseLong(albumId));
        Photo photo = albumService.addPhotoToDir(file, album);
        photo = photoService.save(photo);
        List<Photo> photos = album.getPhotos();
        photos.add(photo);
        album.setPhotos(photos);
        albumService.update(album);
        return null;
    }
}
