package ru.java.mentor.oldranger.club.service.forum.impl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.SubsectionRepository;
import ru.java.mentor.oldranger.club.model.forum.Subsection;
import ru.java.mentor.oldranger.club.service.forum.SubsectionService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SubsectionServiceImpl implements SubsectionService {

    private static final Logger LOG = LoggerFactory.getLogger(SubsectionServiceImpl.class);
    private SubsectionRepository subsectionRepository;


    @Override
    public void createSubsection(Subsection subSection) {
        LOG.info("Saving subsection {}", subSection);
        try {
            subsectionRepository.save(subSection);
            LOG.info("Subsection saved");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public Optional<Subsection> getById(long id) {
        LOG.debug("Getting subsection by id = {}", id);
        return subsectionRepository.findById(id);
    }

    @Override
    public List<Subsection> getAllSubsections() {
        LOG.debug("Getting all subsections");
        List<Subsection> subsections = null;
        try {
            subsections = subsectionRepository.findAll();
            LOG.debug("Returned list of {} subsections", subsections.size());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return subsections;
    }
}
