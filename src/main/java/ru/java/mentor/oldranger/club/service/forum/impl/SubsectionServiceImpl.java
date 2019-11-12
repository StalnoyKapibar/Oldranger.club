package ru.java.mentor.oldranger.club.service.forum.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.SubsectionRepository;
import ru.java.mentor.oldranger.club.model.forum.Subsection;
import ru.java.mentor.oldranger.club.service.forum.SubsectionService;

@Service
public class SubsectionServiceImpl implements SubsectionService {

    private SubsectionRepository subsectionRepository;

    @Autowired
    public SubsectionServiceImpl(SubsectionRepository subsectionRepository) {
        this.subsectionRepository = subsectionRepository;
    }

    @Override
    public void createSubsection(Subsection subSection) {
        subsectionRepository.save(subSection);
    }
}
