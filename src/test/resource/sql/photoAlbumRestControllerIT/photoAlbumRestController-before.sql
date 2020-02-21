insert into jm_oldc_test.roles (id, role) values
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER');

insert into jm_oldc_test.users (id_user, first_name, last_name, email, nick_name,
 password, registered, invite_key, role_id, avatar_id) values
(1, 'Admin', 'Admin', 'admin@javamentor.com', 'Admin', null, null, null, 1, null),
(2, 'User', 'User', 'user@javamentor.com', 'User', null, null, null, 2, null);

insert into jm_oldc_test.media (id, user_id_user) values
(1, 1),
(2, 2);


insert into jm_oldc_test.photo_album (id, allow_view, title, media_id, thumb_image_id) values
(1, 1, 'Album1 Admin', 1, 1),
(2, 1, 'Album2 Admin', 1, null),
(3, 0, 'Album3 Admin', 1, 3);

insert into jm_oldc_test.photos (id, comment_count, description, original_img, small_img, upload_photo_date, album_id) values
(1, null, 'photo 1', 'orig_img 1', 'small_img 1', '2001-01-01 20:00:00', 1),
(2, null, null, null, null, null, 2),
(3, null, 'photo 3', 'orig_img 3', 'small_img 3', '2000-01-02 20:00:00', 3);