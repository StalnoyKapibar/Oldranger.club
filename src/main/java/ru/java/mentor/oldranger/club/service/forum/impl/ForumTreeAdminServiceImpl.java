package ru.java.mentor.oldranger.club.service.forum.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.SectionRepository;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.service.forum.ForumTreeAdminService;

import java.util.List;

@Service
@AllArgsConstructor
public class ForumTreeAdminServiceImpl implements ForumTreeAdminService {

    private SectionRepository sectionRepository;

    @Override
    public List<Section> getAllSections() {
        List<Section> list = sectionRepository.findAllByOrderByPositionAsc();
        return sectionRepository.findAllByOrderByPositionAsc();
    }

    @Override
    public void swapSectons(List<Long> sectionsId) {
        for (int i = 0; i < sectionsId.size(); i++) {
            // (i + 1) - это новая позиция секции с id [i]
            Section section = sectionRepository.getOne(sectionsId.get(i));
            section.setPosition(i + 1);
            sectionRepository.save(section);
        }
    }

}
