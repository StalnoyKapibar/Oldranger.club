package ru.java.mentor.oldranger.club.service.forum;

import ru.java.mentor.oldranger.club.dto.SectionsAndSubsectionsDto;

import java.util.List;

public interface SectionsAndSubsectionsService {

    List<SectionsAndSubsectionsDto> getAllSectionsAndSubsections();

    void swapSections();

}
