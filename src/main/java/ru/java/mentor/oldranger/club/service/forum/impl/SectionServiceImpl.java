package ru.java.mentor.oldranger.club.service.forum.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.SectionRepository;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.service.forum.SectionService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SectionServiceImpl implements SectionService {

    private SectionRepository sectionRepository;

    @Override
    public void addSection(Section section) {
        sectionRepository.save(section);
    }

    @Override
    public Optional<Section> getById(long id) {
        return sectionRepository.findById(id);
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