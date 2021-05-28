package com.mamezou.rms.client.console.ui;

import com.mamezou.rms.client.api.dto.UserAccountClientDto;
import com.mamezou.rms.client.console.ui.TransitionMap.RmsScreen;
import com.mamezou.rms.client.console.ui.TransitionMap.Transition;
import com.mamezou.rms.platform.stopbugs.SuppressFBWarnings;

public class EndScreen implements RmsScreen {

    @Override
    @SuppressFBWarnings("DM_EXIT")
    public Transition play(UserAccountClientDto loginUser, boolean printHeader) {
        System.exit(0);
        return null;
    }
}
