package ru.java.mentor.oldranger.club.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.InviteRepository;
import ru.java.mentor.oldranger.club.model.user.InvitationToken;
import ru.java.mentor.oldranger.club.model.user.User;
import ru.java.mentor.oldranger.club.service.user.InvitationService;

import java.util.Date;
import java.util.List;

@Service
public class InvitationServiceImpl implements InvitationService {
    private InviteRepository repository;

    private static final Long MINUTE = 60000L;
    private static final Long HOUR = 3600000L;
    private static final Long DAY_AND_NIGHT = 3600000L;

    private Long shelfLife = DAY_AND_NIGHT;

    @Autowired
    public InvitationServiceImpl(InviteRepository inviteRepository) {
        this.repository = inviteRepository;
    }

    @Override
    public void save(InvitationToken invitationToken) {
        repository.save(invitationToken);
    }

    @Override
    public List<InvitationToken> findAll() {
        return repository.findAll();
    }

    @Override
    public InvitationToken getInvitationTokenByKey(String key) {
        return repository.findByKey(key);
    }

    @Override
    public void markAsUsed(String key) {
        InvitationToken token = getInvitationTokenByKey(key);
        token.setUsed(true);
        save(token);
    }

    @Override
    public String getCurrentKey(User user) {
        List<InvitationToken> tokens = repository.findAllByUserAndUsedAndMail(user, false, null);
        if (tokens.size() == 0) {
            return null;
        } else {
            String key = tokens.get(0).getKey();
            if (checkShelfLife(tokens.get(0))) {
                markAsUsed(key);
                key = generateKey();
                save(new InvitationToken(key, user));
            }
            return key;
        }

    }

    @Override
    public boolean checkShelfLife(InvitationToken token) {
        String key = token.getKey();
        Date date = getDateCreate(key);
        Long elapsedTime = new Date().getTime() - date.getTime();
        if (elapsedTime >= shelfLife) {
            return true;
        }
        return false;
    }

    @Override
    public void setShelfLife(Long time) {
        shelfLife = time;
    }

    @Override
    public String generateKey() {
        return Math.round((Math.random() * 1000000000)) + "";
    }

    @Override
    public Date getDateCreate(String key) {
        return getInvitationTokenByKey(key).getDate();
    }

    @Override
    public void markInviteOnMailAsUsed(String mail) {
        List<InvitationToken> tokens = repository.findAllByMailAndUsed(mail, false);
        if (tokens.size() != 0) {
            tokens.get(0).setUsed(true);
        }
    }
}
