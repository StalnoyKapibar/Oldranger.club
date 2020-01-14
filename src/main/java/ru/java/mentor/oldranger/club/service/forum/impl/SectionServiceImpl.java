package ru.java.mentor.oldranger.club.service.forum.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.java.mentor.oldranger.club.dao.ForumRepository.SectionRepository;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Subsection;
import ru.java.mentor.oldranger.club.service.forum.SectionService;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class SectionServiceImpl implements SectionService {

    private SectionRepository sectionRepository;

    @Override
    @Transactional
    public void addSection(Section section) {
        log.debug("Saving section {}", section.getName());
        try {
            sectionRepository.save(section);
            log.info("Section saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public Optional<Section> findById(long id) {
        log.debug("Getting section by id {}", id);
        return sectionRepository.findById(id);
    }

    @Override
    public List<Section> getAll() {
        log.debug("Getting all sections");
        List<Section> sections = null;
        try {
            sections = sectionRepository.findAll();;
            log.info("Returned list of {} sections", sections.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return sections;
    }

    @Override
    public List<Section> getAllForAnon() {
        log.debug("Getting all sections for anon");
        List<Section> sections = null;
        try {
            sections = sectionRepository.getAllByHideToAnonIsFalse();;
            log.info("Returned list of {} sections", sections.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return sections;
    }

    @Override
    @Transactional
    public void delete(Section section) {
        log.debug("Deleting section {}", section.getName());
        try {
            sectionRepository.delete(section);
            log.info("Section deleted");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Section update(Section section) {
        log.debug("Updating section with id - {}", section.getId());
        Section newSection = null;
        try {
            newSection = sectionRepository.save(section);
            log.info("Section updated");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return newSection;
    }

    @Override
    public long countAllByName(String name) {
        log.debug("Counting all sections with name {}", name);
        return sectionRepository.countAllByName(name);
    }

    @Override
    public long countAllByPosition(int position) {
        log.debug("Counting all sections with position {}", position);
        return sectionRepository.countAllByPosition(position);
    }
}