package ru.java.mentor.oldranger.club.dao.MediaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.dto.PhotoAlbumDto;
import ru.java.mentor.oldranger.club.model.media.PhotoAlbum;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.Tuple;
import java.util.List;
import java.util.stream.Collectors;

public interface PhotoAlbumRepository extends JpaRepository<PhotoAlbum, Long> {

    //ToDo Cartesian Product problem analysis - ЖЕЛАТЕЛЬНО УДАЛИТЬ ЭТОТ МЕТОД
    @Deprecated
    List<PhotoAlbum> findPhotoAlbumByViewersContainsOrViewersIsNull(User user);

    @Query(nativeQuery = true, value = "SELECT album_id, " +
            "       title, " +
            "       photo_counts, " +
            "       small_img, " +
            "       original_img " +
            "from ( " +
            "         select pa.id       as album_id, " +
            "                pa.title, " +
            "                ph.original_img, " +
            "                ph.small_img, " +
            "                ph.upload_photo_date " +
            "         from photo_album as pa " +
            "                  left join photos as ph on ph.album_id = pa.id " +
            "         where pa.media_id in (select m.user_id_user from media as m where m.user_id_user = ?1) " +
            "           and (ph.original_img IS NOT NULL or ph.small_img IS NOT NULL)) as t1 " +
            "         inner join (SELECT album_id               as oldest_photos_album_id, " +
            "                            MIN(upload_photo_date) as max_date " +
            "                     from (select pa.id as album_id, " +
            "                                  ph.upload_photo_date " +
            "                           from photo_album as pa " +
            "                                    left join photos as ph on ph.album_id = pa.id " +
            "                           where pa.media_id in (select m.user_id_user from media as m where m.user_id_user = ?1) " +
            "                             and (ph.original_img IS NOT NULL or ph.small_img IS NOT NULL)) as oldest_photos_dates_by_album " +
            "                     group by album_id) opdba " +
            "                    on t1.album_id = opdba.oldest_photos_album_id and t1.upload_photo_date = opdba.max_date " +
            "         inner join (SELECT album_id        as photo_counts_album_id, " +
            "                            COUNT(photo_id) as photo_counts " +
            "                     from (select ph.id as photo_id, " +
            "                                  pa.id as album_id " +
            "                           from photo_album as pa " +
            "                                    left join photos as ph on ph.album_id = pa.id " +
            "                           where pa.media_id in (select m.user_id_user from media as m where m.user_id_user = ?1) " +
            "                             and (ph.original_img IS NOT NULL or ph.small_img IS NOT NULL)) as photo_counts_by_album " +
            "                     group by album_id) as pcba on t1.album_id = pcba.photo_counts_album_id " +
            " " +
            "UNION ALL " +
            " " +
            "select id, " +
            "       title, " +
            "       0, " +
            "       ?2, " +
            "       ?2 " +
            "from photo_album as album_without_photos " +
            "where album_without_photos.media_id in (select m.user_id_user from media as m where m.user_id_user = ?1) " +
            "  and id not in (select pa.id as album_id " +
            "                 from photo_album as pa " +
            "                          left join photos as ph on ph.album_id = pa.id " +
            "                 where pa.media_id in (select m.user_id_user from media as m where m.user_id_user = ?1) " +
            "                   and (ph.original_img IS NOT NULL or ph.small_img IS NOT NULL));")
    List<Tuple>  findPhotoAlbumByUserWithPhotoCountAndOldestPhoto(Long userId, String defaultImage);

    default List<PhotoAlbumDto> findPhotoAlbumsByUser(User user) {
        return findPhotoAlbumByUserWithPhotoCountAndOldestPhoto(user.getId(), "photo_album_placeholder").stream()
                .map(e -> new PhotoAlbumDto(
                        e.get("album_id") == null ? null :  Long.valueOf(e.get("album_id").toString()),
                        e.get("title", String.class),
                        e.get("original_img", String.class),
                        e.get("small_img", String.class),
                        e.get("photo_counts") == null ? 0 :  Integer.valueOf(e.get("photo_counts").toString()))).collect(Collectors.toList());
    }

}
