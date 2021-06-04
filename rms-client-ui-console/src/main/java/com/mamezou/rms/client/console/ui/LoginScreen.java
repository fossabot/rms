package com.mamezou.rms.client.console.ui;

import static com.mamezou.rms.client.console.ui.ClientConstants.*;
import static com.mamezou.rms.client.console.ui.textio.TextIoUtils.*;

import javax.enterprise.event.Event;

import com.mamezou.rms.client.api.RentalReservationClientApi;
import com.mamezou.rms.client.api.dto.UserAccountClientDto;
import com.mamezou.rms.client.api.exception.BusinessFlowClientException;
import com.mamezou.rms.client.api.login.LoggedInEvent;
import com.mamezou.rms.client.console.ui.TransitionMap.RmsScreen;
import com.mamezou.rms.client.console.ui.TransitionMap.Transition;
import com.mamezou.rms.platform.env.Environment;

import lombok.RequiredArgsConstructor;

/**
 * アプリケーション開始画面。
 * 開始処理としてのログインを担う
 */
@RequiredArgsConstructor
public class LoginScreen implements RmsScreen {

    private final RentalReservationClientApi clientApi;
    private final Event<LoggedInEvent> loggedInEvent;

    @Override
    public Transition play(UserAccountClientDto dummy, boolean printHeader) {
        try {
            if (printHeader) {
                // 認証画面のヘッダーを表示する
                var jarInfo = Environment.getMainJarInfo();
                println("Version:" + jarInfo.getVersion() + "/Build-Time:" + jarInfo.getBuildtimeInfo());
                println(LOGIN_INFORMATION);
                blankLine();
            }

            var loginId = newStringInputReader()
                    .withMinLength(5)
                    .withMaxLength(10)
                    .withDefaultValue("member1")
                    .read("ログインID");
            if (loginId.equals(SCREEN_BREAK_KEY)) {
                return Transition.END;
            }

            var password = newStringInputReader()
                    .withMinLength(5)
                    .withMaxLength(10)
                    .withDefaultValue("member1")
                    .withInputMasking(true)
                    .read("パスワード");

            // ログイン実行
            var nowLoginUser = clientApi.authenticate(loginId, password);

            // 成功したのでログインユーザをbroadcast
            loggedInEvent.fire(new LoggedInEvent(nowLoginUser));

            return nowLoginUser.getUserType().isAdmin() ? Transition.ADMIN_MAIN : Transition.MEMBER_MAIN;

        } catch (BusinessFlowClientException e) {
            printServerError(e);
            return play(dummy, false);

        }
    }
}
