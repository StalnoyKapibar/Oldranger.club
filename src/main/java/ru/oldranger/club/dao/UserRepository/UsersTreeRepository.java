package ru.oldranger.club.dao.UserRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.oldranger.club.dto.UsersTreeDto;
import ru.oldranger.club.model.user.User;

import javax.persistence.Tuple;
import java.util.List;
import java.util.stream.Collectors;

public interface UsersTreeRepository extends JpaRepository<User, Long> {

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
            "where d.depth <= :deepTree " +
            "ORDER BY depth;")
    List<Tuple> getInvitedUsersTreeById(long userId, long deepTree);

    default List<UsersTreeDto> getInvitedUsersTreeByIdUserDto(long userId, long deepTree) {
        return getInvitedUsersTreeById(userId, deepTree).stream().map(e -> new UsersTreeDto(
                e.get("parent") == null ? null : Long.parseLong(e.get("parent").toString()),
                e.get("nick_name").toString(),
                Long.parseLong(e.get("NEW_USER").toString()),
                Long.parseLong(e.get("depth").toString())
        )).collect(Collectors.toList());
    }
}