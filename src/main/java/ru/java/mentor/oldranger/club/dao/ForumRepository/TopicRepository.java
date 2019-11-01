package ru.java.mentor.oldranger.club.dao.ForumRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.java.mentor.oldranger.club.model.forum.Topic;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    /*
     * Выборка по N первых актуальных (соритировка по дате) Topic (из topics) для каждой Section.
     * */
    @Query(nativeQuery = true,
            value = "select id, is_hide, date_last_message, name_topic, date_start, id_section, user_id " +
                    "from (select *, row_number() over (partition by id_section order by date_last_message desc, id asc) i from topics) t where i <= ?1")
    List<Topic> getTopicsLimitAnyBySection(Integer limitTopicsBySection);

    /*
     * Выборка по N первых актуальных (соритировка по дате) Topic (из topics) для каждой Section.
     * Topic выбираются с условием isHideToAnon = false (значение isHideToAnon в Section не учитывается)
     * */
    @Query(nativeQuery = true,
            value = "select id, is_hide, date_last_message, name_topic, date_start, id_section, user_id " +
                    "from (select *, row_number() over (partition by id_section order by date_last_message desc, id asc) i from topics) t where i <= ?1 and is_hide = false")
    List<Topic> getTopicsLimitAnyBySectionForAnon(Integer limitTopicsBySection);
}
