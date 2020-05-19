package ru.java.mentor.oldranger.club.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;
import ru.java.mentor.oldranger.club.model.utils.BlackList;
import ru.java.mentor.oldranger.club.service.utils.BlackListService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalUnit;
import java.util.*;

public class AuthHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationFailureHandler {

    private ObjectMapper objectMapper = new ObjectMapper();
    private RequestCache requestCache = new HttpSessionRequestCache();

    @Autowired
    private BlackListService blackListService;

    // метод - типа аналог имплементации такового у SavedRequestAwareAuthenticationSuccessHandler,
    // но с вырезанным к чертям редиректом. На текущем фронте (актуально для 11.01.2020) работает
    // В целом отсутсвие редиректа нужно для РЕСТ-аутентификации, с подрубленным кастомным AuthEntryPoint
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest == null) {
            clearAuthenticationAttributes(request);
            return;
        }
        String targetUrlParam = getTargetUrlParameter();
        if (isAlwaysUseDefaultTargetUrl() ||
                (targetUrlParam != null && StringUtils.hasText(request.getParameter(targetUrlParam)))) {
            requestCache.removeRequest(request, response);
            clearAuthenticationAttributes(request);
            return;
        }

        clearAuthenticationAttributes(request);
    }

    // Возвращает HTTP401 и JSON c exception - причиной неудачного входа
    // и timestamp - временной меткой неудачного входа
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
        String username = httpServletRequest.getParameter("username");
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", Calendar.getInstance().getTime());
        data.put("exception", e.getMessage());

        if (username == null) {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            httpServletResponse.getOutputStream()
                    .println(objectMapper.writeValueAsString(data));
        } else {
            List<BlackList> user = blackListService.findByUserName(username);
            if (user.size() == 0) {
                httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                httpServletResponse.getOutputStream()
                        .println(objectMapper.writeValueAsString(data));
            } else {
                LocalDateTime unlockTime = user.get(0).getUnlockTime();
                long milli = unlockTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                data.put("unlockTime", milli);
                httpServletResponse.getOutputStream()
                        .println(objectMapper.writeValueAsString(data));
            }
        }
    }
}
