package ru.java.mentor.oldranger.club.service.forum.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.SubsectionRepository;
import ru.java.mentor.oldranger.club.model.forum.Subsection;
import ru.java.mentor.oldranger.club.service.forum.SubsectionService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SubsectionServiceImpl implements SubsectionService {

    private SubsectionRepository subsectionRepository;

    @Override
    public void createSubsection(Subsection subSection) {
        subsectionRepository.save(subSection);
    }

    @Override
    public Optional<Subsection> getById(long id) {
        return subsectionRepository.findById(id);
    }

    @Override
    public List<Subsection> getAllSubsections() {
        return subsectionRepository.findAll();
    }
}
