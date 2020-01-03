package ru.java.mentor.oldranger.club.service.utils.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.java.mentor.oldranger.club.model.user.User;

@Slf4j
@RequiredArgsConstructor
@Service("expireUsereService")
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionService {

    @NonNull
    private SessionRegistry sessionRegistry;

    @Value("${server.port}")
    String port;

    //Метод для удаления сессии любого пользователя
    public void expireUserSessions(String username) {
        log.info("Expire user {} session", username);
        try {
            for (Object principal : sessionRegistry.getAllPrincipals()) {
                if (principal instanceof User) {
                    UserDetails userDetails = (UserDetails) principal;
                    if (userDetails.getUsername().equals(username)) {
                        for (SessionInformation information : sessionRegistry.getAllSessions(userDetails, true)) {
                            information.expireNow();
                            killExpiredSessionForSure(information.getSessionId());
                        }
                    }
                }
            }
            log.info("Session expired");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    public void killExpiredSessionForSure(String id) {
        log.info("Killing session with id = {}", id);
        try {
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("Cookie", "JSESSIONID=" + id);
            HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
            RestTemplate rt = new RestTemplate();
            rt.exchange("http://localhost:" + port, HttpMethod.GET,
                    requestEntity, String.class);
            log.info("Session killed");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}