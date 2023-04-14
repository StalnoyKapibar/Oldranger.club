package ru.oldranger.club.service.forum.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.oldranger.club.dao.ForumRepository.SectionRepository;
import ru.oldranger.club.model.forum.Section;
import ru.oldranger.club.service.forum.SectionService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class SectionServiceImpl implements SectionService {

    private SectionRepository sectionRepository;

    @Override
    public void addSection(Section section) {
        log.info("Saving section {}", section);
        try {
            sectionRepository.save(section);
            log.info("Section saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public Optional<Section> getById(long id) {
        log.debug("Getting section by id = {}", id);
        return sectionRepository.findById(id);
    }

    @Override
    public List<Section> getAllSections() {
        log.debug("Getting all sections");
        List<Section> sections = null;
        try {
            sections = sectionRepository.findAll();
            log.debug("Returned list of {} sections", sections.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return sections;
    }

    @Override
    public List<Section> getAllSectionsForAnon() {
        log.debug("Getting all sections for anonymous user");
        List<Section> sections = null;
        try {
            sections = sectionRepository.getAllByIsHideToAnonIsFalse();
            log.debug("Returned list of {} sections", sections.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return sections;
    }
}