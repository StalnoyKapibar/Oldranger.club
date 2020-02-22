delete from photos;
delete from photo_album;
delete from users;
delete from media;
delete from roles;

alter table users auto_increment = 0;
alter table roles auto_increment = 0;
alter table media auto_increment = 0;
alter table photos auto_increment = 0;
alter table photo_album auto_increment = 0;