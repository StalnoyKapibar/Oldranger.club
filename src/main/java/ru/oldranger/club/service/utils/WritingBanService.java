package ru.oldranger.club.service.utils;

import ru.oldranger.club.model.user.User;
import ru.oldranger.club.model.utils.BanType;
import ru.oldranger.club.model.utils.WritingBan;

public interface WritingBanService {
    WritingBan getByUserAndType(User user, BanType type);

    void save(WritingBan writingBan);

    boolean isForbidden(User user, BanType type);
}