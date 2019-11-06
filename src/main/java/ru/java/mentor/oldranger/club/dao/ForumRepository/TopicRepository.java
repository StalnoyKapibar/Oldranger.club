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
//            value = "select * " +
//                    "from (select *, row_number() over (partition by id_section order by date_last_message desc, id asc) i from topics) t where i <= ?1")
            value = "select * from (select topics.*, themes.id_section, row_number() over (partition by id_section order by date_last_message desc, id asc) i from topics left join themes on topics.theme_id = themes.id) t where i <= ?1")
    List<Topic> getActualTopicsLimitAnyBySection(Integer limitTopicsBySection);

    /*
     * Выборка по N первых актуальных (соритировка по дате) Topic (из topics) для каждой Section.
     * Topic выбираются с условием isHideToAnon = false (значение isHideToAnon в Section не учитывается)
     * */
    @Query(nativeQuery = true,
//            value = "select * from (select *, row_number() over (partition by id_section order by date_last_message desc, id asc) i from topics where is_hide = false) t where i <= ?1")
            value = "select * from (select topics.*, themes.id_section, row_number() over (partition by id_section order by date_last_message desc, id asc) i from topics left join themes on topics.theme_id = themes.id where topics.is_hide = false) t where i <= ?1")
    List<Topic> getActualTopicsLimitAnyBySectionForAnon(Integer limitTopicsBySection);
}
