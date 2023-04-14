package ru.oldranger.club.service.user.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.oldranger.club.dao.UserRepository.RequestInvitationRepository;
import ru.oldranger.club.model.user.RequestInvitation;
import ru.oldranger.club.service.user.RequestInvitationService;

import java.util.List;

@Service
@AllArgsConstructor
public class RequestInvitationServiceImpl implements RequestInvitationService {
    private RequestInvitationRepository repository;

    @Override
    public void save(RequestInvitation requestInvitation) {
        repository.save(requestInvitation);
    }

    @Override
    public List<RequestInvitation> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(Long theId) {
        repository.deleteById(theId);
    }
}
