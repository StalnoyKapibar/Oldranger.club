package ru.java.mentor.oldranger.club.service.forum.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.SectionRepository;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.service.forum.ForumTreeAdminService;

import java.util.List;
import java.util.Map;

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

    @Override
    public void swapSubsectons(Map<Long, List<String>> sectionsAndSubsectionsIds) {
        // {"1":["subId-3.3","subId-1.1","subId-1.2","subId-1.3"],"3":["subId-3.1","subId-3.2"]}
        for (Map.Entry<Long, List<String>> pair : sectionsAndSubsectionsIds.entrySet()) {
            // получить подсекцию по subId
            // изменить у подсекции секцию и позицию в секции
            // сохранить в БД
            Long sectionId = pair.getKey();
            List<String> subsectionsId = pair.getValue();
        }

    }

}
