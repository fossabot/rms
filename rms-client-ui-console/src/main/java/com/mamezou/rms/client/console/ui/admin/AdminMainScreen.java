package com.mamezou.rms.client.console.ui.admin;

import static com.mamezou.rms.client.console.ui.textio.TextIoUtils.*;

import com.mamezou.rms.client.api.dto.UserAccountClientDto;
import com.mamezou.rms.client.console.ui.TransitionMap.RmsScreen;
import com.mamezou.rms.client.console.ui.TransitionMap.Transition;

import lombok.RequiredArgsConstructor;

public class AdminMainScreen implements RmsScreen {

    @RequiredArgsConstructor
    public enum AdminMenuList {

        ENTRY_RENTAL_ITEM("レンタル品登録", Transition.ENTRY_RENTAL_ITEM),
        ENTRY_USER("ユーザ登録", Transition.ENTRY_USER),
        EDIT_USER("ユーザ編集", Transition.EDIT_USER),
        RELOGIN("再ログイン", Transition.LOGIN),
        END("終了", Transition.END);

        private final String name;
        private final Transition transition;

        Transition getTransition() {
            return transition;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Override
    public Transition play(UserAccountClientDto loginUser, boolean printHeader) {

        printScreenHeader(loginUser, "管理者サービスメニュー画面");

        var selectedMenu = newEnumInputReader(AdminMenuList.class)
                .read("メニュー番号を入力して下さい。");

        return selectedMenu.getTransition();
    }
}
