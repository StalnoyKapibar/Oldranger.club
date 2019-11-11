package ru.java.mentor.oldranger.club.dao.UserRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.dto.UserProfileDto;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.user.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    UserProfile getOneByUser(User user);

    @Query(nativeQuery = true,
            value = "SELECT u.nick_name,u.first_name,u.last_name, up.birthday, up.gender,up.country,up.city,\n" +
                    "       u.email,up.phone_number,up.social_vk,up.social_fb,up.social_tw,\n" +
                    "       u.registered,us.message_count,us.topics_count,us.last_comment,us.last_vizit,up.about_me\n" +
                    "FROM users u\n" +
                    "JOIN user_profile up ON (up.user_id = u.id_user)\n" +
                    "JOIN user_statistic us ON (us.user_id = u.id_user)\n" +
                    "WHERE u.id_user =?1")
    UserProfileDto getUserProfileDto(Long id);
}