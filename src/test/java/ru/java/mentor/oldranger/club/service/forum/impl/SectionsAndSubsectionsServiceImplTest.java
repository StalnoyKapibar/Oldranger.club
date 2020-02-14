package ru.java.mentor.oldranger.club.service.forum.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import ru.java.mentor.oldranger.club.dao.ForumRepository.SectionRepository;
import ru.java.mentor.oldranger.club.dao.ForumRepository.SubsectionRepository;
import ru.java.mentor.oldranger.club.dto.SectionsAndSubsectionsDto;
import ru.java.mentor.oldranger.club.model.forum.Section;
import ru.java.mentor.oldranger.club.model.forum.Subsection;
import ru.java.mentor.oldranger.club.service.forum.SectionsAndSubsectionsService;

import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SectionsAndSubsectionsServiceImplTest {

    @Autowired
    private SectionsAndSubsectionsService sectionsAndSubsectionsService;

    @MockBean
    private SectionRepository sectionRepository;

    @MockBean
    private SubsectionRepository subsectionRepository;

    @Test
    public void getAllSectionsAndSubsectionsNoUniqueSubsectionTest() {
        List<Section> sections = new ArrayList<>();
        sections.add(new Section(1L, "1", 1, true));
        sections.add(new Section(2L, "2", 2, true));
        List<Subsection> subsections = new ArrayList<>();
        subsections.add(new Subsection(1L, "1.1", 1, sections.get(0), true));
        Mockito.when(sectionRepository.findAll((Sort) ArgumentMatchers.any())).thenReturn(sections);
        Mockito.when(subsectionRepository.findAll((Sort) ArgumentMatchers.any())).thenReturn(subsections);
        List<SectionsAndSubsectionsDto> sectionsAndSubsectionsDtos = sectionsAndSubsectionsService.getAllSectionsAndSubsections();
        Assert.assertEquals(sectionsAndSubsectionsDtos.size(), sections.size());
        Assert.assertEquals(sectionsAndSubsectionsDtos.get(0).getSection(), sections.get(0));
        Assert.assertEquals(sectionsAndSubsectionsDtos.get(1).getSection(), sections.get(1));
        Assert.assertEquals(sectionsAndSubsectionsDtos.get(0).getSubsections().size(), subsections.size());
        Assert.assertEquals(sectionsAndSubsectionsDtos.get(0).getSubsections().get(0), subsections.get(0));
        Assert.assertEquals(sectionsAndSubsectionsDtos.get(1).getSubsections().size(), 0);
    }

    @Test
    public void getAllSectionsAndSubsectionsUniqueSubsectionTest() {
        List<Section> sections = new ArrayList<>();
        sections.add(new Section(1L, "1", 1, true));
        sections.add(new Section(2L, "2", 2, true));
        Section uniqueSection = new Section(3L, "3", 3, true);
        List<Subsection> subsections = new ArrayList<>();
        subsections.add(new Subsection(1L, "3.1", 1, uniqueSection, true));
        Mockito.when(sectionRepository.findAll((Sort) ArgumentMatchers.any())).thenReturn(sections);
        Mockito.when(subsectionRepository.findAll((Sort) ArgumentMatchers.any())).thenReturn(subsections);
        List<SectionsAndSubsectionsDto> sectionsAndSubsectionsDtos = sectionsAndSubsectionsService.getAllSectionsAndSubsections();
        Assert.assertEquals(sectionsAndSubsectionsDtos.size(), sections.size());
        Assert.assertEquals(sectionsAndSubsectionsDtos.get(0).getSection(), sections.get(0));
        Assert.assertEquals(sectionsAndSubsectionsDtos.get(1).getSection(), sections.get(1));
        Assert.assertEquals(sectionsAndSubsectionsDtos.get(0).getSubsections().size(), 0);
        Assert.assertEquals(sectionsAndSubsectionsDtos.get(1).getSubsections().size(), 0);
    }

    @Test
    public void swapSectionsTest() {
        Map<Long, Section> sectionMap = new HashMap<>();
        sectionMap.put(1L, new Section(1L, "1", 3, true));
        sectionMap.put(2L, new Section(2L, "2", 4, true));
        Mockito.when(sectionRepository.getOne(ArgumentMatchers.anyLong())).thenAnswer(a -> sectionMap.get(a.getArguments()[0]));
        sectionsAndSubsectionsService.swapSections(sectionMap.keySet().stream().collect(Collectors.toList()));
        Assert.assertEquals(sectionMap.get(1L).getPosition(), 1);
        Assert.assertEquals(sectionMap.get(2L).getPosition(), 2);
        Mockito.verify(sectionRepository, Mockito.times(1)).save(sectionMap.get(1L));
        Mockito.verify(sectionRepository, Mockito.times(1)).save(sectionMap.get(2L));
    }

    @Test
    public void swapSubsectionSuccessTest() {
        Map<Long, Section> sectionMap = new HashMap<>();
        sectionMap.put(1L, new Section(1L, "1", 3, true));
        sectionMap.put(2L, new Section(2L, "2", 4, true));
        Map<Long, Subsection> subsectionMap = new HashMap<>();
        subsectionMap.put(1L, new Subsection(1L, "1.1", 3, sectionMap.get(1L), true));
        subsectionMap.put(2L, new Subsection(2L, "1.2", 3, sectionMap.get(1L), true));
        subsectionMap.put(3L, new Subsection(3L, "2.3", 3, sectionMap.get(1L), true));
        Mockito.when(sectionRepository.getOne(ArgumentMatchers.anyLong())).thenAnswer(a -> sectionMap.get(a.getArguments()[0]));
        Mockito.when(subsectionRepository.getOne(ArgumentMatchers.anyLong())).thenAnswer(a -> subsectionMap.get(a.getArguments()[0]));
        Map<Long, List<String>> sectionIDS = new HashMap<>();
        sectionIDS.put(1L, Arrays.asList("1.1", "1.2"));
        sectionIDS.put(2L, Arrays.asList("2.3"));
        sectionsAndSubsectionsService.swapSubsectons(sectionIDS);
        Assert.assertEquals(subsectionMap.get(1L).getPosition(), 1);
        Assert.assertEquals(subsectionMap.get(2L).getPosition(), 2);
        Assert.assertEquals(subsectionMap.get(3L).getPosition(), 1);
        Assert.assertEquals(subsectionMap.get(3L).getSection(), sectionMap.get(2L));
        subsectionMap.forEach((k, v) -> Mockito.verify(subsectionRepository, Mockito.times(1)).save(v));

    }

    @Test
    public void swapSubsectionFailureTest() {
        Map<Long, Section> sectionMap = new HashMap<>();
        sectionMap.put(1L, new Section(1L, "1", 3, true));
        sectionMap.put(2L, new Section(2L, "2", 4, true));
        Map<Long, Subsection> subsectionMap = new HashMap<>();
        subsectionMap.put(1L, new Subsection(1L, "1,1", 3, sectionMap.get(1L), true));
        subsectionMap.put(2L, new Subsection(2L, "1,2", 3, sectionMap.get(1L), true));
        subsectionMap.put(3L, new Subsection(3L, "2,3", 3, sectionMap.get(1L), true));
        Mockito.when(sectionRepository.getOne(ArgumentMatchers.anyLong())).thenAnswer(a -> sectionMap.get(a.getArguments()[0]));
        Mockito.when(subsectionRepository.getOne(ArgumentMatchers.anyLong())).thenAnswer(a -> subsectionMap.get(a.getArguments()[0]));
        Map<Long, List<String>> sectionIDS = new HashMap<>();
        sectionIDS.put(1L, Arrays.asList("1,1", "1,2"));
        sectionIDS.put(2L, Arrays.asList("2,3"));
        sectionsAndSubsectionsService.swapSubsectons(sectionIDS);
        subsectionMap.forEach((k, v) -> Assert.assertEquals(v.getPosition(), 3));
        Assert.assertEquals(subsectionMap.get(3L).getSection(), sectionMap.get(1L));
    }
}
