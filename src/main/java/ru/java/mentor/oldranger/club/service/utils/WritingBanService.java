package ru.java.mentor.oldranger.club.service.utils;

import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BanType;
import ru.java.mentor.oldranger.club.model.utils.WritingBan;

import java.util.List;

public interface WritingBanService {
    WritingBan getByUserAndType(User user, BanType type);

    void save(WritingBan writingBan);

    boolean isForbidden(User user, BanType type);

    List<BanType> getByUser(User user);

    void deleteMute(User user);
}