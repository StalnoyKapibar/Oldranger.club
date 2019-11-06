package ru.java.mentor.oldranger.club.service.forum.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.ForumRepository.ThemeRepository;
import ru.java.mentor.oldranger.club.model.forum.Theme;
import ru.java.mentor.oldranger.club.service.forum.ThemeService;

@Service
public class ThemeServiceImpl implements ThemeService {

    private ThemeRepository themeRepository;

    @Autowired
    public ThemeServiceImpl(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    @Override
    public void createTheme(Theme theme) {
        themeRepository.save(theme);
    }
}
