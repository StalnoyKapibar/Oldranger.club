package ru.java.mentor.oldranger.club.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.java.mentor.oldranger.club.dto.PhotoAlbumDto;

import java.util.ArrayList;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/config/datasource-test.properties")
@WithUserDetails("Admin")
@Sql(value = "/sql/photoAlbumRestControllerIT/photoAlbumRestController-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/sql/photoAlbumRestControllerIT/photoAlbumRestController-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PhotoAlbumRestControllerIT {

    @Autowired
    PhotoAlbumRestController controller;
    @Autowired
    MockMvc mockMvc;

    @Test
    void getPhotoAlbums() throws Exception {
        String json = new ObjectMapper().writeValueAsString(
                new ArrayList<>(Arrays.asList(
                        new PhotoAlbumDto(1L, "Album1 Admin", "orig_img 1", "small_img 1", 1),
                        new PhotoAlbumDto(2L, "Album2 Admin", "thumb_image_placeholder", "thumb_image_placeholder", 1)
                )));

        mockMvc.perform(get("/api/albums"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    @WithAnonymousUser
    void getPhotoAlbumsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/albums"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void savePhotoAlbum() throws Exception {
        String json = new ObjectMapper().writeValueAsString(
                new PhotoAlbumDto(4L, "Album 4", "thumb_image_placeholder", "thumb_image_placeholder", 0));

        mockMvc.perform(post("/api/albums")
                .param("albumTitle", "Album 4"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/albums/4"))
                .andExpect(content().json(json));
    }

    @Test
    void savePhotoAlbumWithEmptyTitle() throws Exception{
        mockMvc.perform(post("/api/albums")
                .param("albumTitle", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void savePhotoAlbumWithoutAuth() throws Exception{
        mockMvc.perform(post("/api/albums")
                .param("albumTitle", "Album 4"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAlbum() throws Exception {
        String json = new ObjectMapper().writeValueAsString(
                new PhotoAlbumDto(1L, "Album1 Admin", "orig_img 1", "small_img 1", 1));

        mockMvc.perform(get("/api/albums/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    @WithAnonymousUser
    void getAlbumWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/albums/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAlbumWithInvalidAlbumId() throws Exception {
        mockMvc.perform(get("/api/albums/4"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("User")
    void getAlbumWithForbiddenUser() throws Exception {
        mockMvc.perform(get("/api/albums/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPhotosByAlbum() throws Exception {
        mockMvc.perform(get("/api/albums/getPhotos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].description").value("photo 1"));
    }

    @Test
    @WithAnonymousUser
    void getPhotosByAlbumWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/albums/getPhotos/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPhotosByAlbumWithInvalidAlbumId() throws Exception {
        mockMvc.perform(get("/api/albums/getPhotos/4"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("User")
    void getPhotosByAlbumWithForbiddenUser() throws Exception {
        mockMvc.perform(get("/api/albums/getPhotos/1"))
                .andExpect(status().isBadRequest());
    }


    @Test
    void updateAlbum() throws Exception {
        String json = new ObjectMapper().writeValueAsString(
                new PhotoAlbumDto(1L, "Changed title", "orig_img 3", "small_img 3", 1));

        mockMvc.perform(put("/api/albums/1")
                .param("photoId", "3")
                .param("title", "Changed title"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/albums/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    @WithAnonymousUser
    void updateAlbumWithoutAuth() throws Exception {
        mockMvc.perform(put("/api/albums/1")
                .param("photoId", "3")
                .param("title", "Changed title"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAlbumWithInvalidAlbumId() throws Exception {
        mockMvc.perform(put("/api/albums/4")
                .param("photoId", "3")
                .param("title", "Changed title"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAlbumWithInvalidPhotoId() throws Exception {
        mockMvc.perform(put("/api/albums/1")
                .param("photoId", "4")
                .param("title", "Changed title"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAlbumWithInvalidTitle() throws Exception {
        mockMvc.perform(put("/api/albums/1")
                .param("photoId", "3")
                .param("title", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("User")
    void updateAlbumWithForbiddenUser() throws Exception {
        mockMvc.perform(put("/api/albums/1")
                .param("photoId", "3")
                .param("title", "Changed title"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAlbum() throws Exception {
        String json = new ObjectMapper().writeValueAsString(
                new ArrayList<>(Arrays.asList(
                        new PhotoAlbumDto(2L, "Album2 Admin", "thumb_image_placeholder", "thumb_image_placeholder", 1))));

        mockMvc.perform(delete("/api/albums/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/albums"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    @WithAnonymousUser
    void deleteAlbumWithoutAuth() throws Exception {
        mockMvc.perform(delete("/api/albums/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAlbumWithInvalidAlbumId() throws Exception {
        mockMvc.perform(delete("/api/albums/4"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("User")
    void deleteAlbumWithForbiddenUser() throws Exception {
        mockMvc.perform(delete("/api/albums/1"))
                .andExpect(status().isBadRequest());
    }

}