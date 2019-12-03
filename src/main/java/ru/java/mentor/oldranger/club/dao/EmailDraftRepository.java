package ru.java.mentor.oldranger.club.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.java.mentor.oldranger.club.model.utils.EmailDraft;

public interface EmailDraftRepository extends JpaRepository<EmailDraft, Long> {
}
