package ru.java.mentor.oldranger.club.service.mail.impl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.java.mentor.oldranger.club.dao.EmailDraftRepository;
import ru.java.mentor.oldranger.club.model.utils.EmailDraft;
import ru.java.mentor.oldranger.club.service.forum.impl.TopicVisitAndSubscriptionServiceImpl;
import ru.java.mentor.oldranger.club.service.mail.EmailDraftService;

import java.util.List;

@Service
@AllArgsConstructor
public class EmailDraftServiceImpl implements EmailDraftService {

    private static final Logger LOG = LoggerFactory.getLogger(TopicVisitAndSubscriptionServiceImpl.class);
    EmailDraftRepository emailDraftRepository;

    @Override
    public Page<EmailDraft> getAllDraftsPageable(Pageable pageable) {
        LOG.debug("Getting page {} of email drafts", pageable.getPageNumber());
        Page<EmailDraft> page = null;
        try {
            page = emailDraftRepository.findAll(pageable);
            LOG.debug("Page returned");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return page;
    }

    @Override
    public List<EmailDraft> getAllDrafts() {
        LOG.debug("Getting all email drafts");
        List<EmailDraft> drafts = null;
        try {
            drafts = emailDraftRepository.findAll(Sort.by("lastEditDate").descending());
            LOG.debug("Returned list of {} drafts", drafts.size());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return drafts;
    }

    @Override
    public EmailDraft saveDraft(EmailDraft draft) {
        LOG.info("Saving email draft {}", draft);
        try {
            draft = emailDraftRepository.save(draft);
            LOG.info("Email draft saved");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return draft;
    }

    @Override
    public EmailDraft updateDraft(EmailDraft draft) {
        LOG.info("Updating email draft with id = {}", draft.getId());
        try {
            draft = emailDraftRepository.save(draft);
            LOG.info("Email draft updated");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return draft;
    }

    @Override
    public void deleteDraft(long id) {
        LOG.info("Deleting email draft with id = {}", id);
        try {
            emailDraftRepository.deleteById(id);
            LOG.info("Email draft deleted");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public EmailDraft getById(long id) {
        LOG.debug("Getting draft with id = {}", id);
        EmailDraft draft = null;
        try {
            draft = emailDraftRepository.getOne(id);
            LOG.debug("Email draft returned");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return draft;
    }
}
