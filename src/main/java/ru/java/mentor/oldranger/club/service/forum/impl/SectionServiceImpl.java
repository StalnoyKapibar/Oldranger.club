package ru.java.mentor.oldranger.club.service.forum.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.SectionRepository;
import ru.java.mentor.oldranger.club.dto.SectionDto;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.service.forum.SectionService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class SectionServiceImpl implements SectionService {

    private SectionRepository sectionRepository;

    @Override
    public SectionDto addSection(Section section) {
        SectionDto dto = null;
        Section savedSection = null;
        log.info("Saving section {}", section);
        try {
            savedSection = sectionRepository.save(section);
            dto = new SectionDto(savedSection.getId(), savedSection.getName(), savedSection.getPosition());
            log.info("Section saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return dto;
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
            sections = sectionRepository.getAllByIsHideToAnonIsFalse(Sort.by("position").ascending());
            log.debug("Returned list of {} sections", sections.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return sections;
    }

    @Override
    public void updateSectionsPosition(int position) {
        log.debug("Update section`s position by one");
        sectionRepository.updateSectionsPosition(position);
    }

    @Override
    public int findMaxPosition() {
        log.debug("Find maximal position");
        return sectionRepository.findMaxPosition();
    }

    @Override
    public List<Section> findSectionsByName(String name) {
        log.debug("Find list of sections by name");
        return sectionRepository.findSectionsByName(name);
    }
}