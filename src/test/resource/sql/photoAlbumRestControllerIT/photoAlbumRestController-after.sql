delete from photos;
delete from photo_album;
delete from users;
delete from media;
delete from roles;
delete from photo_album_viewers;
delete from photo_album_writers;

alter table users auto_increment = 0;
alter table roles auto_increment = 0;
alter table media auto_increment = 0;
alter table photos auto_increment = 0;
alter table photo_album auto_increment = 0;
alter table photo_album_viewers auto_increment = 0;
alter table photo_album_writers auto_increment = 0;