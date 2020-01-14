package ru.java.mentor.oldranger.club.service.forum.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.java.mentor.oldranger.club.dao.ForumRepository.SubsectionRepository;
import ru.java.mentor.oldranger.club.model.forum.Section;
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
    @Transactional
    public void add(Subsection subsection) {
        log.debug("Saving subsection {}", subsection.getName());
        try {
            subsectionRepository.save(subsection);
            log.info("Subsection saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
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
            log.info("Returned list of {} subsections", subsections.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return subsections;
    }

    @Override
    public List<Subsection> getAllSubsectionsForAnon() {
        log.debug("Getting all subsections for anon");
        List<Subsection> subsections = null;
        try {
            subsections = subsectionRepository.getAllByHideToAnonIsFalse();
            log.info("Returned list of {} anon subsections", subsections.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return subsections;
    }

    @Override
    public List<Subsection> getAllSubsectionsForAnonBySection(Section section) {
        log.debug("Getting all subsections of current section for anon");
        List<Subsection> subsections = null;
        try {
            subsections = subsectionRepository.getAllByHideToAnonIsFalseAndSection(section);
            log.info("Returned list of {} subsections of section - {}", subsections.size(), section.getName());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return subsections;
    }

    @Override
    public List<Subsection> getAllSubsectionsBySection(Section section) {
        log.debug("Getting all subsections of current section");
        List<Subsection> subsections = null;
        try {
            subsections = subsectionRepository.getAllBySection(section);
            log.info("Returned list of {} subsections of section - {}", subsections.size(), section.getName());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return subsections;
    }

    @Override
    @Transactional
    public void deleteSubsection(Subsection subsection) {
        log.debug("deleting subsection - {}", subsection.getName());
        try {
            subsectionRepository.delete(subsection);
            log.info("Subsections {}, was deleted", subsection.getName());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Subsection updateSubsection(Subsection subsection) {
        log.debug("updating subsection - {}", subsection.getName());
        Subsection newSubsection = null;
        try {
             newSubsection = subsectionRepository.save(subsection);
            log.info("Subsections {}, was updated", subsection.getName());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return newSubsection;
    }

    @Override
    public long countAllByName(String name) {
        log.debug("Counting subsection by name = {}", name);
        return subsectionRepository.countAllByName(name);
    }

    @Override
    public long countAllByPosition(int position) {
        log.debug("Counting subsection by position = {}", position);
        return subsectionRepository.countAllByPosition(position);
    }
}
