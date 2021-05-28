package com.mamezou.rms.client.api.login;

import com.mamezou.rms.client.api.dto.UserAccountClientDto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ログイン成功時にアプリケーションから通知されるイベントクラス。
 */
@Getter
@RequiredArgsConstructor
public class LoggedInEvent {

    private final UserAccountClientDto loginUser;

}
