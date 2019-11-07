package ru.java.mentor.oldranger.club.service.forum.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.SectionRepository;
import ru.java.mentor.oldranger.club.dto.SectionsAndTopicsDto;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.service.forum.ForumTreeAdminService;
import ru.java.mentor.oldranger.club.service.forum.SectionsAndTopicsService;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ForumTreeAdminServiceImpl implements ForumTreeAdminService {

    private SectionsAndTopicsService sectionsAndTopicsService;

    private SectionRepository sectionRepository;

    @Override
    public List<SectionsAndTopicsDto> getAllSectionAndAllTopics() {
        return sectionsAndTopicsService.getAllSectionsAndActualTopicsLimit10BySection();
    }

    @Override
    public void swapSectons(Map<String, Long> swapSections) {
        try {
            Long id = swapSections.get("id1");
            Long newPosition = swapSections.get("newPosition1");
            Section section = sectionRepository.getOne(id);
            section.setPosition(newPosition.intValue());
            sectionRepository.save(section);

            id = swapSections.get("id2");
            newPosition = swapSections.get("newPosition2");
            section = sectionRepository.getOne(id);
            section.setPosition(newPosition.intValue());
            sectionRepository.save(section);

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

}
