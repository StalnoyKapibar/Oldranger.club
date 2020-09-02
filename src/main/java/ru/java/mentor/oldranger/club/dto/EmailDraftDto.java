package ru.java.mentor.oldranger.club.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.java.mentor.oldranger.club.model.utils.EmailDraft;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailDraftDto {
    private EmailDraft emailDraft;
    private String[] roles;
}
