package ru.oldranger.club.service.mail;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.oldranger.club.model.utils.EmailDraft;

import java.util.List;

public interface EmailDraftService {
    EmailDraft saveDraft(EmailDraft draft);

    EmailDraft updateDraft(EmailDraft draft);

    List<EmailDraft> getAllDrafts();

    Page<EmailDraft> getAllDraftsPageable(Pageable pageable);

    EmailDraft getById(long id);

    void deleteDraft(long id);
}
