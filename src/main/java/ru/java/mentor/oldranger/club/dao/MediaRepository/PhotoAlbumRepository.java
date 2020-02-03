package ru.java.mentor.oldranger.club.dao.MediaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.java.mentor.oldranger.club.dto.ArticleTagsNodeDto;
import ru.java.mentor.oldranger.club.dto.PhotoAlbumDto;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface PhotoAlbumRepository extends JpaRepository<PhotoAlbum, Long> {

    List<PhotoAlbum> findPhotoAlbumByViewersContainsOrViewersIsNull(User user);

    @Query(nativeQuery = true, value = "SELECT album_id, " +
            "       title, " +
            "       media_id, " +
            "       photo_id, " +
            "       description, " +
            "       photo_counts, " +
            "       upload_photo_date, " +
            "       small_img, " +
            "       original_img " +
            "from ( " +
            "         select pa.id       as album_id, " +
            "                pa.title, " +
            "                pa.media_id, " +
            "                ph.id       as photo_id, " +
            "                ph.description, " +
            "                ph.original_img, " +
            "                ph.small_img, " +
            "                ph.upload_photo_date, " +
            "                ph.album_id as photo_album_id " +
            "         from photo_album as pa " +
            "                  left join photos as ph on ph.album_id = pa.id " +
            "         where pa.media_id in (select m.user_id_user from media as m where m.user_id_user = :userId) " +
            "           and (ph.original_img IS NOT NULL or ph.small_img IS NOT NULL)) as t1 " +
            "         inner join (SELECT album_id               as oldest_photos_album_id, " +
            "                            MIN(upload_photo_date) as min_date " +
            "                     from (select pa.id as album_id, " +
            "                                  ph.upload_photo_date " +
            "                           from photo_album as pa " +
            "                                    left join photos as ph on ph.album_id = pa.id " +
            "                           where pa.media_id in (select m.user_id_user from media as m where m.user_id_user = :userId) " +
            "                             and (ph.original_img IS NOT NULL or ph.small_img IS NOT NULL) " +
            "                           order by pa.id, ph.upload_photo_date) as oldest_photos_dates_by_album " +
            "                     group by album_id) opdba " +
            "                    on t1.album_id = opdba.oldest_photos_album_id and t1.upload_photo_date = opdba.min_date " +
            "         inner join (SELECT album_id        as photo_counts_album_id, " +
            "                            COUNT(photo_id) as photo_counts " +
            "                     from (select ph.id as photo_id, " +
            "                                  pa.id as album_id " +
            "                           from photo_album as pa " +
            "                                    left join photos as ph on ph.album_id = pa.id " +
            "                           where pa.media_id in (select m.user_id_user from media as m where m.user_id_user = :userId) " +
            "                             and (ph.original_img IS NOT NULL or ph.small_img IS NOT NULL) " +
            "                           order by pa.id, ph.upload_photo_date) as photo_counts_by_album " +
            "                     group by album_id) as pcba on t1.album_id = pcba.photo_counts_album_id;")
    List<Tuple>  findPhotoAlbumByUserWithPhotoCountAndOldestPhoto(Long userId);

    default List<PhotoAlbumDto> findPhotoAlbumsByUser( User user) {
        return findPhotoAlbumByUserWithPhotoCountAndOldestPhoto(user.getId()).stream()
                .map(e -> new PhotoAlbumDto(
                        e.get("album_id") == null ? null :  Long.valueOf(e.get("album_id").toString()),
                        e.get("title", String.class),
                        e.get("original_img", String.class),
                        e.get("small_img", String.class),
                        e.get("photo_counts") == null ? null :  Integer.valueOf(e.get("album_id").toString()))).collect(Collectors.toList());
    }

}
