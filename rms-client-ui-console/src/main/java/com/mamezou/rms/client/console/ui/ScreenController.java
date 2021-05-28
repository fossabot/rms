package com.mamezou.rms.client.console.ui;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.mamezou.rms.client.api.RentalReservationClientApi;
import com.mamezou.rms.client.api.login.LoggedInEvent;
import com.mamezou.rms.client.console.login.LoginEventReciever;
import com.mamezou.rms.client.console.ui.TransitionMap.RmsScreen;
import com.mamezou.rms.client.console.ui.TransitionMap.Transition;
import com.mamezou.rms.client.console.ui.admin.AdminMainScreen;
import com.mamezou.rms.client.console.ui.admin.EditUserScreen;
import com.mamezou.rms.client.console.ui.admin.EntryRentalItemScreen;
import com.mamezou.rms.client.console.ui.admin.EntryUserScreen;
import com.mamezou.rms.client.console.ui.member.CancelReservationScreen;
import com.mamezou.rms.client.console.ui.member.EntryReservationScreen;
import com.mamezou.rms.client.console.ui.member.InquiryReservationScreen;
import com.mamezou.rms.client.console.ui.member.MemberMainScreen;

/**
 * アプリケーションの画面遷移を制御するクラス
 */
@ApplicationScoped
public class ScreenController {

    private TransitionMap transitionMap;
    private LoginEventReciever loginEventReciever;

    @Inject
    public ScreenController(RentalReservationClientApi clientApi, Event<LoggedInEvent> event, LoginEventReciever reciever) {
        this.transitionMap = new TransitionMap();
        transitionMap.add(Transition.LOGIN, new LoginScreen(clientApi, event));
        transitionMap.add(Transition.MEMBER_MAIN, new MemberMainScreen());
        transitionMap.add(Transition.INQUIRY_RESERVATION, new InquiryReservationScreen(clientApi));
        transitionMap.add(Transition.ENTRY_RESERVATRION, new EntryReservationScreen(clientApi));
        transitionMap.add(Transition.CANCEL_RESERVATRION, new CancelReservationScreen(clientApi));
        transitionMap.add(Transition.ADMIN_MAIN, new AdminMainScreen());
        transitionMap.add(Transition.ENTRY_RENTAL_ITEM, new EntryRentalItemScreen(clientApi));
        transitionMap.add(Transition.ENTRY_USER, new EntryUserScreen(clientApi));
        transitionMap.add(Transition.EDIT_USER, new EditUserScreen(clientApi));
        transitionMap.add(Transition.END, new EndScreen());
        loginEventReciever = reciever;
    }

    public void start(String[] args) {
        var loginScreen = transitionMap.stratScreen();
        doPlay(loginScreen);
    }

    private RmsScreen doPlay(RmsScreen screen) {
        var next = screen.play(loginEventReciever.getLoginUser(), true);
        return doPlay(transitionMap.nextScreen(next)) ;
    }
}
