package ru.java.mentor.oldranger.club.sevice.ForumService.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.SectionRepository;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.sevice.ForumService.SectionService;

import java.util.List;

@Service
public class SectionServiceImpl implements SectionService {

    private SectionRepository sectionRepository;

    @Autowired
    public void setSectionRepository(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @Override
    public void addSection(Section section) {
        sectionRepository.save(section);
    }

    @Override
    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    @Override
    public List<Section> getAllSectionsForAnon() {
        return sectionRepository.getAllByIsHideToAnonIsFalse();
    }
}
