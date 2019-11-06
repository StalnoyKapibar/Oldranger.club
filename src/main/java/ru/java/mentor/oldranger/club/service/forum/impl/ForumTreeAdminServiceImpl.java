package ru.java.mentor.oldranger.club.service.forum.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dto.SectionsAndTopicsDto;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Topic;
import ru.java.mentor.oldranger.club.service.forum.ForumTreeAdminService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class ForumTreeAdminServiceImpl implements ForumTreeAdminService {

    @Override
    public List<SectionsAndTopicsDto> getAllSectionAndAllTopics() {

        // временно для тестирования вывода данных из БД на страницу
        Section section1 = new Section("1 section", 1, false);
        List<Topic> topics1 = new ArrayList<>();
        topics1.add(new Topic("1.1 topic", null, null, null, section1, false));
        topics1.add(new Topic("1.2 topic", null, null, null, section1, false));
        topics1.add(new Topic("1.3 topic", null, null, null, section1, false));

        Section section2 = new Section("2 section", 2, false);
        List<Topic> topics2 = new ArrayList<>();
        topics2.add(new Topic("2.1 topic", null, null, null, section2, false));
        topics2.add( new Topic("2.2 topic", null, null, null, section2, false));
        topics2.add( new Topic("2.3 topic", null, null, null, section2, false));

        Section section3 = new Section("3 section", 3, false);
        List<Topic> topics3 = new ArrayList<>();
        topics3.add(new Topic("3.1 topic", null, null, null, section3, false));
        topics3.add(new Topic("3.2 topic", null, null, null, section3, false));
        topics3.add(new Topic("3.3 topic", null, null, null, section3, false));

        SectionsAndTopicsDto dto1 = new SectionsAndTopicsDto(section1, topics1);
        SectionsAndTopicsDto dto2 = new SectionsAndTopicsDto(section2, topics2);
        SectionsAndTopicsDto dto3 = new SectionsAndTopicsDto(section3, topics3);

        List<SectionsAndTopicsDto> list = new ArrayList<>();
        list.add(dto1);
        list.add(dto2);
        list.add(dto3);
        return list;
    }
}
