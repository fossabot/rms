package com.mamezou.rms.client.console.ui.admin;

import static com.mamezou.rms.client.console.ui.ClientConstants.*;
import static com.mamezou.rms.client.console.ui.textio.TextIoUtils.*;

import com.mamezou.rms.client.api.RentalReservationClientApi;
import com.mamezou.rms.client.api.dto.RentalItemClientDto;
import com.mamezou.rms.client.api.dto.UserAccountClientDto;
import com.mamezou.rms.client.api.exception.BusinessFlowClientException;
import com.mamezou.rms.client.console.ui.ClientConstants;
import com.mamezou.rms.client.console.ui.TransitionMap.RmsScreen;
import com.mamezou.rms.client.console.ui.TransitionMap.Transition;
import com.mamezou.rms.client.console.ui.textio.RmsStringInputReader.PatternMessage;
import com.mamezou.rms.client.console.ui.textio.TextIoUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntryRentalItemScreen implements RmsScreen {

    private final RentalReservationClientApi clientApi;

    @Override
    public Transition play(UserAccountClientDto loginUser, boolean printHeader) {

        if (printHeader) {
            printScreenHeader(loginUser, "レンタル品登録画面");
        }

        // 入力インフォメーションの表示
        println(ENTRY_RENTAL_ITEM_INFORMATION);

        // シリアル番号の入力
        var serialNo = newStringInputReader()
                .withMinLength(1)
                .withMaxLength(15)
                .withPattern(PatternMessage.SERIAL_NO)
                .read("シリアル番号");
        if (TextIoUtils.isBreak(serialNo)) {
            return Transition.ADMIN_MAIN;
        }

        // 品名の入力
        var itemName = newStringInputReader()
                .withMaxLength(15)
                .withDefaultValue("")
                .read("品名（空白可）");

        blankLine();

        // レンタル品登録の実行
        try {
            var addItem = RentalItemClientDto.ofTransient(serialNo, itemName);
            var newItem = clientApi.addRentalItem(addItem);
            printResultInformation(loginUser, newItem);
            return Transition.ADMIN_MAIN;

        } catch (BusinessFlowClientException e) {
            printServerError(e);
            return play(loginUser, false); // start over!!

        }
    }

    private void printResultInformation(UserAccountClientDto loginUser, RentalItemClientDto newItem) {
        println("***** レンタル品登録結果 *****");
        printf(ClientConstants.RENATL_ITEM_FORMAT.format(newItem));
        blankLine();
        waitPressEnter();
    }
}
