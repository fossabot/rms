package com.mamezou.rms.core;

import static com.mamezou.rms.core.TestUtils.*;
import static com.mamezou.rms.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.mamezou.rms.core.TestUtils.PathResolverParameterExtension;
import com.mamezou.rms.core.common.LoginUserUtils;
import com.mamezou.rms.core.common.ServiceLoginUser;
import com.mamezou.rms.core.domain.RentalItem;
import com.mamezou.rms.core.domain.Reservation;
import com.mamezou.rms.core.domain.UserAccount;
import com.mamezou.rms.core.domain.UserAccount.UserType;
import com.mamezou.rms.core.exception.BusinessFlowException;
import com.mamezou.rms.core.persistence.file.io.PathResolver;

@ExtendWith(PathResolverParameterExtension.class)
class RentalReservationApplicationTest {

    private RentalReservationApplication target;

    @BeforeEach
    void setup(PathResolver pathResolver) throws Exception {
        target = newRentalReservationApplication(pathResolver);
    }

    @Test
    void testAuthenticate() {
        var expect = UserAccount.of(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
        var actual = target.authenticate("member1", "member1");
        assertThatToString(actual).isEqualTo(expect);
    }

    @ParameterizedTest
    @CsvSource({ "soramame, hoge", "hoge, soramame", "hoge, hoge" })
    void testCannotAuthenticate(String id, String password) {
        catchThrowableOfType(() ->
            target.authenticate(id, password),
            BusinessFlowException.class
        );
    }

    @Test
    void testGetAllRentalItems() {
        var expect = List.of(
                RentalItem.of(1, "A0001", "レンタル品1号"),
                RentalItem.of(2, "A0002", "レンタル品2号"),
                RentalItem.of(3, "A0003", "レンタル品3号"),
                RentalItem.of(4, "A0004", "レンタル品4号")
            );
        var actual = target.getAllRetailItems();
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }

    @Test
    void testGetAllUserAccounts() {
        var expected = List.of(
                UserAccount.of(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER),
                UserAccount.of(2, "member2", "member2", "メンバー2", "080-1111-2222", "連絡先2", UserType.MEMBER),
                UserAccount.of(3, "admin", "admin", "管理者", "050-1111-2222", "連絡先3", UserType.ADMIN)
                );
        var actual = target.getAllUserAccounts();
        assertThatToString(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void testFindReservationByRentalItemAndStartDate() {
        var expect = List.of(
                Reservation.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1),
                Reservation.of(2, LocalDateTime.of(2020, 4, 1, 16, 0, 0), LocalDateTime.of(2020, 4, 1, 18, 0, 0), "メモ2", 3, 2)
            );
        var actual = target.findReservationByRentalItemAndStartDate(3, LocalDate.of(2020, 4, 1));
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }

    @ParameterizedTest
    @CsvSource({ "903, 2004/04/01", "1, 2004/07/10", "903, 2004/07/10" })
    void testCannotFindReservationByRentalItemAndStartDate(int rentalItemId, String date) {
        var pttn = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        catchThrowableOfType(() ->
            target.findReservationByRentalItemAndStartDate(rentalItemId, LocalDate.parse(date, pttn)),
            BusinessFlowException.class
        );
    }

    @Test
    void testAddReservation() {
        var addReservation = Reservation.ofTransient(LocalDateTime.of(2021, 4, 18, 10, 0, 0), LocalDateTime.of(2021, 5, 16, 20, 0, 0), "メモ4", 3, 1);
        var expect = Reservation.of(4, LocalDateTime.of(2021, 4, 18, 10, 0, 0), LocalDateTime.of(2021, 5, 16, 20, 0, 0), "メモ4", 3, 1);
        var actual = target.addReservation(addReservation);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testFailToAddReservationForBadItem() {
        // rentalItemId=999はマスタ登録なし
        var addReservation = Reservation.of(null, LocalDateTime.of(2021, 4, 18, 10, 0, 0), LocalDateTime.of(2021, 5, 16, 20, 0, 0), "メモ4", 999, 1);
        catchThrowableOfType(() ->
            target.addReservation(addReservation),
            BusinessFlowException.class
        );
    }

    @Test
    void testFailToAddReservationForDuplicate() {
        // 2020/4/1 16:00-18:00 で既に予約あり
        var addReservation = Reservation.of(null, LocalDateTime.of(2020, 4, 1, 17, 0, 0), LocalDateTime.of(2020, 4, 1, 19, 0, 0), "メモ4", 3, 1);
        catchThrowableOfType(() ->
            target.addReservation(addReservation),
            BusinessFlowException.class
        );
    }

    @Test
    void testFindReservationByReserverId() {
        // 1件ヒットパターン
        var actual = target.findReservationByReserverId(2);
        assertThat(actual).hasSize(1);
        // 2件ヒットパターン
        actual = target.findReservationByReserverId(1);
        assertThat(actual).hasSize(2);
        // 0件ヒットパターン
        actual = target.findReservationByReserverId(3);
        assertThat(actual).isEmpty();
    }

    @Test
    void testAddRentalItem() {
        var addRentalItem = RentalItem.ofTransient("A0005", "レンタル品5号");
        var expect = RentalItem.of(5, "A0005", "レンタル品5号");
        var actual = target.addRentalItem(addRentalItem);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testFailToAddRentalItem() {
        // "A0004"は既に登録済みのSerialNo
        var addRentalItem = RentalItem.ofTransient("A0004", "レンタル品5号");
        catchThrowableOfType(() ->
            target.addRentalItem(addRentalItem),
            BusinessFlowException.class
        );
    }

    @Test
    void testAddUserAccount() {
        var addUserAccount = UserAccount.ofTransient("member3", "member3", "メンバー3", "030-1111-2222", "連絡先4", UserType.MEMBER);
        var expect = UserAccount.of(4, "member3", "member3", "メンバー3", "030-1111-2222", "連絡先4", UserType.MEMBER);
        UserAccount actual = target.addUserAccount(addUserAccount);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testFailToAddUserAccount() {
        // "member1"のloginIdは既に登録済み
        var addUserAccount = UserAccount.ofTransient("member1", "member3", "メンバー3", "030-1111-2222", "連絡先4", UserType.MEMBER);
        catchThrowableOfType(() ->
            target.addUserAccount(addUserAccount),
            BusinessFlowException.class
        );
    }

    @Test
    void testCancelReservation() {
        LoginUserUtils.set(ServiceLoginUser.of(2, null)); // 事前条件
        target.cancelReservation(2);
    }

    @Test
    void testUserAccopuntUpdate() {
        var updateUser = target.get(UserAccount.class, 1);
        updateUser.setUserName("UPDATE");
        var resultUser = target.updateUserAccount(updateUser);

        assertThat(resultUser.getUserName()).isEqualTo("UPDATE");
        assertThatToString(resultUser).isEqualTo(target.get(UserAccount.class, 1));
    }

    @Test
    void testFailToUpdateUserAccount() {
        var updateUser = UserAccount.of(999, "member1", "member3", "メンバー3", "030-1111-2222", "連絡先4", UserType.MEMBER);
        catchThrowableOfType(() ->
            target.updateUserAccount(updateUser),
            BusinessFlowException.class
        );
    }
}
