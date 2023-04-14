package ru.oldranger.club.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class SocketSecurityConfig
        extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(
            MessageSecurityMetadataSourceRegistry messages) {
        messages
                .simpSubscribeDestMatchers("/chanel/private/**").authenticated()
                .anyMessage().authenticated();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
