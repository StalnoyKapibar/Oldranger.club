package ru.java.mentor.oldranger.club.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import ru.java.mentor.oldranger.club.model.media.Photo;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.media.PhotoAlbumService;
import ru.java.mentor.oldranger.club.service.media.PhotoService;
import ru.java.mentor.oldranger.club.service.user.UserService;

import java.util.List;

@Controller
public class TestAlbumsController {
    private PhotoAlbumService albumService;
    private PhotoService photoService;
    private UserService userService;
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

    @Autowired
    protected void setUserService(UserService service) {
        this.userService = service;
    }

    @Value("${server.protocol}")
    private String protocol;

    @Value("${server.name}")
    private String host;

    @Value("${server.port}")
    private String port;

    @GetMapping("/albums")
    public ModelAndView returnPageAlbums(String title) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByNickName(userName);
        List<PhotoAlbum> albums = user.getMedia().getPhotoAlbums();
        ModelAndView model = new ModelAndView();
        model.addObject("albums", albums);
        model.setViewName("albums");
        return model;
    }

    @PostMapping("/create")
    public String createAlbum(String title) {
        PhotoAlbum album;
        if (title == null) {
            album = new PhotoAlbum("Без имени");
        } else {
            album = new PhotoAlbum(title);
        }
        albumService.save(album);
        return "redirect:albums";
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
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("album");
        modelAndView.addObject("album", album);
        return modelAndView;
    }

    @RequestMapping(value = "deleteAlbum/{id}", method = RequestMethod.GET)
    public String deleteAlbum(@PathVariable("id") String id) {
        albumService.deleteAlbum(Long.parseLong(id));
        return "redirect:/albums";
    }

    @RequestMapping(value = "deletePhoto/{albumId}/{photoId}", method = RequestMethod.GET)
    public String deleteAlbum(@PathVariable("albumId") String albumId, @PathVariable("photoId") String photoId) {
        Photo photo = photoService.findById(Long.parseLong(photoId));
        PhotoAlbum album = albumService.findById(Long.parseLong(albumId));
        albumService.deletePhotoFromDir(album, photo);
        photoService.deletePhoto(Long.parseLong(photoId));
        return "redirect:/album/" + albumId;
    }
}
