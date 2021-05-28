package com.mamezou.rms.client.console.login;

import java.time.LocalDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import com.mamezou.rms.client.api.dto.UserAccountClientDto;
import com.mamezou.rms.client.api.login.JsonWebTokenConsumeEvent;
import com.mamezou.rms.client.api.login.LoggedInEvent;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Getter
@Slf4j
public class LoginEventReciever {

    private UserAccountClientDto loginUser;
    private LocalDateTime loggedInAt;
    private String jsonWebToken;


    // -----------------------------------------------------  add observer methods

    void onRecieveTokenEvent(@Observes JsonWebTokenConsumeEvent event) {
        log.debug("イベント受信 event->JsonWebTokenConsumeEvent");
        this.jsonWebToken = event.getToken().getValue();
    }

    void onLoggedInEvent(@Observes LoggedInEvent event) {
        log.debug("イベント受信 event->LoggedInEvent");
        this.loginUser = event.getLoginUser();
        this.loggedInAt = LocalDateTime.now();
    }
}
