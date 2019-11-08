package ru.java.mentor.oldranger.club.service.forum;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.java.mentor.oldranger.club.dto.TopicAndNewMessagesCountDto;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.model.user.User;

import java.util.List;

public interface TopicService {

    void createTopic(Topic topic);

    void editTopicByName(Topic topic);

    void deleteTopicById(Long id);

    Topic findById(Long id);

    List<Topic> getActualTopicsLimitAnyBySection(Integer limitTopicsBySection);

    List<Topic> getActualTopicsLimitAnyBySectionForAnon(Integer limitTopicsBySection);

    List<Topic> getActualTopicsLimit10BySection();

    List<Topic> getActualTopicsLimit10BySectionForAnon();

    /**
     * Пейджинг для подсекций самостоятельно определяющий
     * использовать метод для анонима или для неанонима.
     */
    Page<Topic> getPageableBySubsection(Section subsection, Pageable pageable);

    /**
     * Пейджинг для подсекций для анонимов.<br>
     * Сортировка: по дате последнего сообщения (новые сверху).
     */
    Page<Topic> getPageableBySubsectionForAnon(Section subsection, Pageable pageable);

    /**
     * Пейджинг для подсекций для неанонимов.<br>
     * Порядок сортировки:<br>
     * 1. Элементы с подпиской пользователя, где есть новые сообщения &ndash; сверху.<br>
     * 2. По дате последнего сообщения (новые сверху).
     */
    Page<Topic> getPageableBySubsectionForUser(User user, Section subsection, Pageable pageable);
}
