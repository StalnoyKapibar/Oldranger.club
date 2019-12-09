package ru.java.mentor.oldranger.club.service.utils.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.WritingBanRepository;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BanType;
import ru.java.mentor.oldranger.club.model.utils.WritingBan;
import ru.java.mentor.oldranger.club.service.utils.WritingBanService;

@Service
public class WritingBanServiceImpl implements WritingBanService {
    @Autowired
    WritingBanRepository repository;

    @Override
    public WritingBan getByUserAndType(User user, BanType type) {
        return repository.findByUserAndBanType(user, type);
    }

    @Override
    public void save(WritingBan writingBan) {
        repository.save(writingBan);
    }
}
