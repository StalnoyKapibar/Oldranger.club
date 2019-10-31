package ru.java.mentor.oldranger.club.sevice.ForumService;

import ru.java.mentor.oldranger.club.model.forum.Section;

import java.util.List;

public interface SectionService {

    void addSection(Section section);

    List<Section> getAllSections();

    List<Section> getAllSectionsForAnon();
}
