package ru.java.mentor.oldranger.club.service.mail.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.EmailDraftRepository;
import ru.java.mentor.oldranger.club.model.utils.EmailDraft;
import ru.java.mentor.oldranger.club.service.mail.EmailDraftService;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class EmailDraftServiceImpl implements EmailDraftService {

    EmailDraftRepository emailDraftRepository;

    @Override
    public Page<EmailDraft> getAllDraftsPageable(Pageable pageable) {
        log.debug("Getting page {} of email drafts", pageable.getPageNumber());
        Page<EmailDraft> page = null;
        try {
            page = emailDraftRepository.findAll(pageable);
            log.debug("Page returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return page;
    }

    @Override
    public List<EmailDraft> getAllDrafts() {
        log.debug("Getting all email drafts");
        List<EmailDraft> drafts = null;
        try {
            drafts = emailDraftRepository.findAll(Sort.by("lastEditDate").descending());
            log.debug("Returned list of {} drafts", drafts.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return drafts;
    }

    @Override
    public EmailDraft saveDraft(EmailDraft draft) {
        log.info("Saving email draft {}", draft);
        try {
            draft = emailDraftRepository.save(draft);
            log.info("Email draft saved");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return draft;
    }

    @Override
    public EmailDraft updateDraft(EmailDraft draft) {
        log.info("Updating email draft with id = {}", draft.getId());
        try {
            draft = emailDraftRepository.save(draft);
            log.info("Email draft updated");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return draft;
    }

    @Override
    public void deleteDraft(long id) {
        log.info("Deleting email draft with id = {}", id);
        try {
            emailDraftRepository.deleteById(id);
            log.info("Email draft deleted");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public EmailDraft getById(long id) {
        log.debug("Getting draft with id = {}", id);
        EmailDraft draft = null;
        try {
            draft = emailDraftRepository.getOne(id);
            log.debug("Email draft returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return draft;
    }
}
