package ru.java.mentor.oldranger.club.service.forum.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.SubsectionRepository;
import ru.java.mentor.oldranger.club.dto.SubsectionDto;
import ru.java.mentor.oldranger.club.model.forum.Subsection;
import ru.java.mentor.oldranger.club.service.forum.SubsectionService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class SubsectionServiceImpl implements SubsectionService {

    private SubsectionRepository subsectionRepository;


    @Override
    public SubsectionDto createSubsection(Subsection subSection) {
        SubsectionDto dto = null;
        log.info("Saving subsection {}", subSection);
        try {
            subsectionRepository.save(subSection);
            dto = new SubsectionDto(subSection.getSection(), subSection.getName(), subSection.getPosition());
            log.info("Subsection saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return dto;
    }

    @Override
    public Optional<Subsection> getById(long id) {
        log.debug("Getting subsection by id = {}", id);
        return subsectionRepository.findById(id);
    }

    @Override
    public List<Subsection> getAllSubsections() {
        log.debug("Getting all subsections");
        List<Subsection> subsections = null;
        try {
            subsections = subsectionRepository.findAll();
            log.debug("Returned list of {} subsections", subsections.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return subsections;
    }

    @Override
    public void updateSubsectionsPosition(int position) {
        log.debug("Update subsection`s position by one");
        subsectionRepository.updateSubsectionsPosition(position);
    }

    @Override
    public int findMaxPosition() {
        log.debug("Find maximal position");
        return subsectionRepository.findMaxPosition();
    }

    @Override
    public List<Subsection> findSubsectionsByName(String name) {
        log.debug("Find list of subsections by name");
        return subsectionRepository.findSubsectionsByName(name);
    }
}
