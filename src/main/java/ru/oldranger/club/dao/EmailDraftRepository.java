package ru.oldranger.club.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.oldranger.club.model.utils.EmailDraft;

public interface EmailDraftRepository extends JpaRepository<EmailDraft, Long> {
}
