package ru.java.mentor.oldranger.club.dao.UserRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.model.user.User;

import javax.persistence.Tuple;
import java.util.List;

public interface UsersTreeRepository extends JpaRepository<User, Long> {

    // where id_user in (select new_user from invitation_token where user = 1)
//    @Query(nativeQuery = true, value = "select u.nick_name, u.invite_key, t.user from users u left join invitation_token t on u.id_user = t.new_user")
//    @Query(nativeQuery = true, value = "select u.id_user, u.nick_name, u.invite_key, t.user  " +
//            "from users u  " +
//            "         inner join invitation_token t  " +
//            "                    on u.id_user = t.new_user  " +
//            "where id_user in (select new_user from invitation_token where user = ?1)")
    @Query(nativeQuery = true, value = "WITH RECURSIVE " +
            " " +
            "    descendantCategories AS (SELECT k.id_invite, k.user, k.new_user, 0 AS depth " +
            "                             FROM (SELECT null as id_invite, null as user, id_user as new_user " +
            "                                   FROM users " +
            "                                   where id_user not in (select new_user from invitation_token) " +
            "                                     and id_user in (select user from invitation_token) " +
            "                                   union all " +
            "                                   SELECT id_invite, user, new_user " +
            "                                   FROM invitation_token) as k " +
            "                             WHERE new_user = :userId " +
            "                             UNION ALL " +
            "                             SELECT c.id_invite, c.user, c.new_user, dc.depth + 1 " +
            "                             FROM descendantCategories AS dc " +
            "                                      JOIN (SELECT null as id_invite, null as user, id_user as new_user " +
            "                                            FROM users " +
            "                                            where id_user not in (select new_user from invitation_token) " +
            "                                              and id_user in (select user from invitation_token) " +
            "                                            union all " +
            "                                            SELECT id_invite, user, new_user " +
            "                                            FROM invitation_token) AS c ON dc.new_user = c.user) " +
            "SELECT d.id_invite, " +
            "       d.user     as parent, " +
            "       d.new_user as NEW_USER, " +
            "       U.email    as USER_EMAIL, " +
            "       U.nick_name, " +
            "       d.depth " +
            "FROM descendantCategories as d " +
            "         left join users as U on d.new_user = U.id_user " +
            "ORDER BY depth;")
    List<Tuple> getInvitedUsersTreeById(long userId);
}