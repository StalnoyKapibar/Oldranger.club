package ru.oldranger.club.dao.MediaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.oldranger.club.dto.PhotoAlbumDto;
import ru.oldranger.club.model.media.PhotoAlbum;
import ru.oldranger.club.model.user.User;

import javax.persistence.Tuple;
import java.util.List;
import java.util.stream.Collectors;

public interface PhotoAlbumRepository extends JpaRepository<PhotoAlbum, Long> {

    List<PhotoAlbum> findPhotoAlbumByViewersContainsOrViewersIsNull(User user);

    PhotoAlbum findPhotoAlbumByTitle(String title);

    @Query(nativeQuery = true, value = "select  " +
            "  pa.id as album_id,  " +
            "  title,  " +
            "  CASE  " +
            "  WHEN original_img is null  " +
            "    THEN ?2  " +
            "  ELSE original_img  " +
            "  END   as original_img,  " +
            "  CASE  " +
            "  WHEN small_img is null  " +
            "    THEN ?2  " +
            "  ELSE small_img  " +
            "  END   as small_img,  " +
            "  CASE  " +
            "  WHEN counter.photos_count is null  " +
            "    THEN 0  " +
            "  ELSE counter.photos_count  " +
            "  END   as photos_count  " +

            "from photo_album as pa  " +
            "  left join photos as ph on thumb_image_id = ph.id  " +
            "  left join (  " +
            "              select  " +
            "                count(ph.id) as photos_count,  " +
            "                ph.album_id  as album_id  " +
            "              from photos as ph  " +
            "              where ph.album_id in (  " +
            "                select pa.id  " +
            "                from photo_album as pa  " +
            "                  left join photos as ph on thumb_image_id = ph.id  " +
            "                where media_id in (select id as media_id_id  " +
            "                                   from media  " +
            "                                   where user_id_user = ?1)  " +
            "              ) " +
            "              group by album_id  " +
            "            ) as counter on pa.id = counter.album_id  " +

            "where media_id in (select id as media_id_id  " +
            "                   from media  " +
            "                   where user_id_user = ?1) and allow_view=1;")
    List<Tuple> findPhotoAlbumsByUserWithPhotoCount(Long userId, String defaultImage);

    default List<PhotoAlbumDto> findPhotoAlbumsDtoOwnedByUser(User user) {
        return findPhotoAlbumsByUserWithPhotoCount(user.getId(), "thumb_image_placeholder").stream()
                .map(e -> new PhotoAlbumDto(
                        e.get("album_id") == null ? null : Long.valueOf(e.get("album_id").toString()),
                        e.get("title", String.class),
                        e.get("original_img", String.class),
                        e.get("small_img", String.class),
                        e.get("photos_count") == null ? 0 : Integer.valueOf(
                                e.get("photos_count").toString()))).collect(Collectors.toList());
    }

}