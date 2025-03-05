package com.reinertisa.sta.event.listener;

import com.reinertisa.sta.event.UserEvent;
import com.reinertisa.sta.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventListener {
    private final EmailService emailService;

    @EventListener
    public void onUserEvent(UserEvent event) {
        switch (event.getType()) {
            case REGISTRATION -> emailService.setNewAccountEmail(
                    event.getUser().getFirstName(),
                    event.getUser().getEmail(),
                    (String)event.getData().get("key")
            );
            case RESET_PASSWORD -> emailService.sendPasswordResetEmail(
                    event.getUser().getFirstName(),
                    event.getUser().getEmail(),
                    (String)event.getData().get("key")
            );
            default -> {}
        }
    }
}
