package ru.oldranger.club.dao.ForumRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.oldranger.club.model.forum.Subsection;
import ru.oldranger.club.model.forum.Topic;
import ru.oldranger.club.model.user.User;
import ru.oldranger.club.projection.IdAndNumberProjection;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    Page<Topic> findAllBytopicStarter(User user, Pageable pageable);

    /*
     * Выборка по N первых актуальных (соритировка по дате) Topic (из topics) для каждой Section.
     * */
    @Query(nativeQuery = true,
//            value = "select * " +
//                    "from (select *, row_number() over (partition by id_section order by date_last_message desc, id asc) i from topics) t where i <= ?1")
            value = "select * from (select topics.*, subsections.id_section, row_number() over (partition by id_section order by date_last_message desc, id asc) i from topics left join subsections on topics.subsection_id = subsections.id) t where i <= ?1")
    List<Topic> getActualTopicsLimitAnyBySection(Integer limitTopicsBySection);

    /*
     * Выборка по N первых актуальных (соритировка по дате) Topic (из topics) для каждой Section.
     * Topic выбираются с условием isHideToAnon = false (значение isHideToAnon в Section не учитывается)
     * */
    @Query(nativeQuery = true,
//            value = "select * from (select *, row_number() over (partition by id_section order by date_last_message desc, id asc) i from topics where is_hide = false) t where i <= ?1")
            value = "select * from (select topics.*, subsections.id_section, row_number() over (partition by id_section order by date_last_message desc, id asc) i from topics left join subsections on topics.subsection_id = subsections.id where topics.is_hide = false) t where i <= ?1")
    List<Topic> getActualTopicsLimitAnyBySectionForAnon(Integer limitTopicsBySection);

    /*
     * Выборка по N первых актуальных (соритировка по дате последнего сообщения) Topic (из topics).
     * */
    @Query(nativeQuery = true,
            value = "select * from topics order by date_last_message desc limit ?1")
    List<Topic> getActualTopicsLimit(Integer limitTopics);

    /*
     * Выборка по N первых актуальных (соритировка по дате последнего сообщения) Topic (из topics).
     * Topic выбираются с условием isHideToAnon = false (значение isHideToAnon в Section не учитывается)
     * */
    @Query(nativeQuery = true,
            value = "select * from topics t where t.is_hide = false order by date_last_message desc limit ?1")
    List<Topic> getActualTopicsLimitForAnon(Integer limitTopics);

    /**
     * Подмножество Topics для выбранной Subsection, которые помечены для анонимов.
     * Отсортированно по дате последнего сообщения в Topic.
     */
    Page<Topic> findBySubsectionAndIsHideToAnonIsFalseOrderByLastMessageTimeDesc(Subsection subsection, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from topics as t " +
            "where t.date_last_message < ?2 and t.subsection_id = ?1 and t.is_hide=0 " +
            "order by t.date_last_message DESC")
    Page<Topic> findBySubsectionWithTimeForAnon(Subsection subsection, String date, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from topics as topic " +
            "where topic.date_last_message < ?2 and topic.subsection_id = ?1 " +
            "order by topic.date_last_message DESC")
    Page<Topic> findBySubsectionWithTimeForUser(Subsection subsection, String date, Pageable pageable);

    /**
     * Используется для неанонимов.<br>
     * Возвращает подмножество Topics для выбранной id Subsection с эмуляцией пагинации.<br>
     * Порядок сортировки:<br>
     * 1. Элементы с подпиской пользователя, где есть новые сообщения &ndash; сверху.<br>
     * 2. Общая сортировка по дате последнего сообщения (свежие сверху).<br>
     * <b>Важно</b>: Pageable в чистом виде с таким запросом не работает.
     * Поэтому используется дополнительный метод<br>
     * {@code long countForGetSliceListBySubsectionForUserOrderByLastMessageTimeDescAndSubscriptionsWithNewMessagesFirst}
     * чтобы получить общее количество элементов Topics для эмуляции пейджинга.
     */
    @Query(nativeQuery = true,
            value = "select *, (comb.id_user is not null and comb.is_subscribed=true and (comb.date_last_message>=comb.date_lastvisit or comb.date_lastvisit is null)) as subscriber_has_new_msg from " +
                    "(select * from topics t left join (select topic_id, id_user, date_lastvisit, is_subscribed from topic_visit_and_subscriptions s where s.id_user=?1 group by s.topic_id) st on t.id=st.topic_id " +
                    "where t.subsection_id=?2) as comb " +
                    "order by subscriber_has_new_msg desc, comb.date_last_message desc " +
                    "limit ?3,?4")
    List<Topic> getSliceListBySubsectionForUserOrderByLastMessageTimeDescAndSubscriptionsWithNewMessagesFirst(long userId, long subsectionId, int offset, int limit);

    /**
     * Подсчёт общего количества элементов для выборок<br>
     * {@code List<Topic> getSliceListBySubsectionForUserOrderByLastMessageTimeDescAndSubscriptionsWithNewMessagesFirst}
     */
    @Query(nativeQuery = true,
            value = "select count(*) from " +
                    "(select * from topics t left join " +
                    "(select topic_id from topic_visit_and_subscriptions s where s.id_user=?1 group by s.topic_id) " +
                    "st on t.id=st.topic_id where t.subsection_id=?2) as comb")
    long countForGetSliceListBySubsectionForUserOrderByLastMessageTimeDescAndSubscriptionsWithNewMessagesFirst(long userId, long subsectionId);

    /**
     * Подсчёт общего количества сообщений для списка идентификаторов класса Topic<br>
     * Если сообщений в Топике нет, то пара с id Топика будет отсутствовать в выбоке!
     *
     * @param ids список id для Topic
     * @return пара {@code IdAndNumberProjection} "id Топика" - "общее количество сообщений в Топике"
     */
    @Query(nativeQuery = true,
            value = "select id_topic as id, count(*) as number from comments c where c.id_topic in :ids group by id_topic")
    List<IdAndNumberProjection> getPairsTopicIdAndTotalMessagesCount(List<Long> ids);


    /**
     * Подсчёт новых сообщений для пользователя User для списка идентификаторов класса Topic<br>
     * Если новых сообщений в Топике нет, то пара с id Топика будет отсутствовать в выбоке!
     *
     * @param ids    список id для топика Topic
     * @param userId id пользователя User
     * @return пара {@code IdAndNumberProjection} "id Топика" - "количество новых сообщений для пользователя в Топике"
     */
    @Query(nativeQuery = true,
            value = "select c.id_topic as id, count(*) as number from comments c cross join topic_visit_and_subscriptions t on c.id_topic=t.topic_id " +
                    "where t.id_user=?2 and t.topic_id in ?1 and c.date_comment>t.date_lastvisit " +
                    "group by c.id_topic")
    List<IdAndNumberProjection> getPairsTopicIdAndNewMessagesCountForUserId(List<Long> ids, Long userId);
}
