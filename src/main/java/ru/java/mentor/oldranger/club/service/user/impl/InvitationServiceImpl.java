package ru.java.mentor.oldranger.club.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.UserRepository.InviteRepository;
import ru.java.mentor.oldranger.club.model.user.InvitationToken;
import ru.java.mentor.oldranger.club.service.user.InvitationService;

import java.util.List;

@Service
public class InvitationServiceImpl implements InvitationService {
    private InviteRepository repository;

    private String currentKey = null;

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
    public String getCurrentKey() {
        if (currentKey == null) {
            currentKey = Math.round((Math.random() * 1000000000)) + "";
            return currentKey;
        } else {
            if (getInvitationTokenByKey(currentKey).getUsed()) {
                currentKey = Math.round((Math.random() * 1000000000)) + "";
                return currentKey;
            }
            return null;
        }
    }
}
