package ru.java.mentor.oldranger.club.service.utils.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.WritingBanRepository;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BanType;
import ru.java.mentor.oldranger.club.model.utils.WritingBan;
import ru.java.mentor.oldranger.club.service.utils.WritingBanService;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class WritingBanServiceImpl implements WritingBanService {
    WritingBanRepository repository;

    @Override
    public WritingBan getByUserAndType(User user, BanType type) {
        return repository.findByUserAndBanType(user, type);
    }

    @Override
    public void save(WritingBan writingBan) {
        repository.save(writingBan);
    }

    @Override
    public boolean isForbidden(User user, BanType type) {
        boolean isForbidden = false;
        if (user == null) {
            isForbidden = true;
        } else {
            WritingBan writingBan = repository.findByUserAndBanType(user, type);
            if (writingBan != null && (writingBan.getUnlockTime() == null || writingBan.getUnlockTime().isAfter(LocalDateTime.now()))) {
                isForbidden = true;
            }
        }
        return isForbidden;
    }
}
