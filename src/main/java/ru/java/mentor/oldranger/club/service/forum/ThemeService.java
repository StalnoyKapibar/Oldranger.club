package ru.java.mentor.oldranger.club.service.forum;

import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.model.forum.Theme;

@Service
public interface ThemeService {

    void createTheme(Theme theme);

}
