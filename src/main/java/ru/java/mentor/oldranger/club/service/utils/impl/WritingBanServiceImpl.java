package ru.java.mentor.oldranger.club.service.utils.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.WritingBanRepository;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.model.utils.BanType;
import ru.java.mentor.oldranger.club.model.utils.WritingBan;
import ru.java.mentor.oldranger.club.service.utils.WritingBanService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class WritingBanServiceImpl implements WritingBanService {

    WritingBanRepository repository;

    @Override
    public WritingBan getByUserAndType(User user, BanType type) {
        log.debug("Getting writing ban for user with id = {}", user.getId());
        WritingBan ban = null;
        try {
            ban = repository.findByUserAndBanType(user, type);
            log.debug("Writing ban returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ban;
    }

    @Override
    public void save(WritingBan writingBan) {
        log.info("Saving writing ban {}", writingBan);
        try {
            repository.save(writingBan);
            log.info("Writing ban saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
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

    @Override
    public List<BanType> getByUser(User user) {
        log.debug("Getting writing ban for user with id = {}", user.getId());
        List<BanType> ban = null;
        try {
            ban = repository.findByUserId(user.getId());

            log.debug("Writing ban returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ban;
    }

    @Transactional
    @Override
    public void deleteMute(User user) {
        log.info("Deleting user mute by user = {}", user);
        try {
            repository.deleteWritingBansByUser(user);
            log.debug("User mute deleted");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
