package ru.java.mentor.oldranger.club.service.mail.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.EmailDraftRepository;
import ru.java.mentor.oldranger.club.model.utils.EmailDraft;
import ru.java.mentor.oldranger.club.service.mail.EmailDraftService;

import java.util.List;

@Service
@AllArgsConstructor
public class EmailDraftServiceImpl implements EmailDraftService {

    EmailDraftRepository emailDraftRepository;

    @Override
    public Page<EmailDraft> getAllDraftsPageable(Pageable pageable) {
        return emailDraftRepository.findAll(pageable);
    }

    @Override
    public List<EmailDraft> getAllDrafts() {
        return emailDraftRepository.findAll(Sort.by("lastEditDate").descending());
    }

    @Override
    public EmailDraft saveDraft(EmailDraft draft) {
       return emailDraftRepository.save(draft);
    }

    @Override
    public EmailDraft updateDraft(EmailDraft draft) {
        return emailDraftRepository.save(draft);
    }

    @Override
    public void deleteDraft(long id) {
        emailDraftRepository.deleteById(id);
    }

    @Override
    public EmailDraft getById(long id) {
        return emailDraftRepository.getOne(id);
    }
}
