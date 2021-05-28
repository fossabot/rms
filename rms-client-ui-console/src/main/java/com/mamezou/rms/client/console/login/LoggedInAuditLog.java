package com.mamezou.rms.client.console.login;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import com.mamezou.rms.client.api.login.LoggedInEvent;

import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class LoggedInAuditLog {

    void onLoggedInEvent(@Observes LoggedInEvent event) {
        log.info("{}さんがログインしました！", event.getLoginUser().getUserName());
    }
}
