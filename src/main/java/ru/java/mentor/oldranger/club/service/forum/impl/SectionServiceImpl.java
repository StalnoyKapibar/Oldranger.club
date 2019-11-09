package ru.java.mentor.oldranger.club.service.forum.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.SectionRepository;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.service.forum.SectionService;

import java.util.List;

@Service
public class SectionServiceImpl implements SectionService {

    @Autowired
    private SectionRepository sectionRepository;

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