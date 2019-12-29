package ru.java.mentor.oldranger.club.service.forum.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.SubsectionRepository;
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
    public void createSubsection(Subsection subSection) {
        log.info("Saving subsection {}", subSection);
        try {
            subsectionRepository.save(subSection);
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
            log.debug("Returned list of {} subsections", subsections.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return subsections;
    }
}
