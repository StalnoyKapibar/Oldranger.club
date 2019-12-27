package ru.java.mentor.oldranger.club.service.forum.impl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.SectionRepository;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.service.forum.SectionService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SectionServiceImpl implements SectionService {

    private static final Logger LOG = LoggerFactory.getLogger(SectionServiceImpl.class);
    private SectionRepository sectionRepository;

    @Override
    public void addSection(Section section) {
        LOG.info("Saving section {}", section);
        try {
            sectionRepository.save(section);
            LOG.info("Section saved");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public Optional<Section> getById(long id) {
        LOG.debug("Getting section by id = {}", id);
        return sectionRepository.findById(id);
    }

    @Override
    public List<Section> getAllSections() {
        LOG.debug("Getting all sections");
        List<Section> sections = null;
        try {
            sections = sectionRepository.findAll();
            LOG.debug("Returned list of {} sections", sections.size());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return sections;
    }

    @Override
    public List<Section> getAllSectionsForAnon() {
        LOG.debug("Getting all sections for anonymous user");
        List<Section> sections = null;
        try {
            sections = sectionRepository.getAllByIsHideToAnonIsFalse();
            LOG.debug("Returned list of {} sections", sections.size());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return sections;
    }
}