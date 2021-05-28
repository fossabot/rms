package com.mamezou.rms.core.persistence;

import static com.mamezou.rms.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.mamezou.rms.core.TestUtils.PathResolverParameterExtension;
import com.mamezou.rms.core.domain.Reservation;
import com.mamezou.rms.test.junit5.TransactionalForTest;

@ExtendWith(PathResolverParameterExtension.class)
public abstract class AbstractReservationRepositoryTest {

    protected abstract ReservationRepository repository();

    @Test
    void testGet() {
        var expect = Reservation.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1);
        var actual = repository().get(1);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testGetNull() {
        var reservationId = 9999; // 該当なし
        var actual = repository().get(reservationId);
        assertThat(actual).isNull();
    }

    @Test
    void testFindAll() {
        var expected = List.of(
                Reservation.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1),
                Reservation.of(2, LocalDateTime.of(2020, 4, 1, 16, 0, 0), LocalDateTime.of(2020, 4, 1, 18, 0, 0), "メモ2", 3, 2),
                Reservation.of(3, LocalDateTime.of(2021, 4, 1, 10, 0, 0), LocalDateTime.of(2021, 4, 1, 12, 0, 0), "メモ3", 3, 1)
                );
        var actuals = repository().findAll();
        assertThatToString(actuals).containsExactlyElementsOf(expected);
    }

    @Test
    void testFindByRentalItemAndStartDate() {
        // 1件ヒットパターン
        var expect = List.of(
                Reservation.of(3, LocalDateTime.of(2021, 4, 1, 10, 0, 0), LocalDateTime.of(2021, 4, 1, 12, 0, 0), "メモ3", 3, 1)
            );
        var actual = repository().findByRentalItemAndStartDate(3, LocalDate.of(2021, 4, 1));
        assertThatToString(actual).containsExactlyElementsOf(expect);
        // 2件ヒットパターン
        expect = List.of(
                Reservation.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1),
                Reservation.of(2, LocalDateTime.of(2020, 4, 1, 16, 0, 0), LocalDateTime.of(2020, 4, 1, 18, 0, 0), "メモ2", 3, 2)
            );
        actual = repository().findByRentalItemAndStartDate(3, LocalDate.of(2020, 4, 1));
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }

    @Test
    void testFindEmptyByRentalItemAndStartDate() {
        // 該当なし(開始日)
        var actual = repository().findByRentalItemAndStartDate(1, LocalDate.of(2999, 4, 1));
        assertThat(actual).isEmpty();
        // 該当なし(レンタル品ID)
        actual = repository().findByRentalItemAndStartDate(999, LocalDate.of(2020, 4, 1));
        assertThat(actual).isEmpty();
    }

    @Test
    void testFindByUserAccountId() {
        // 1件ヒットパターン
        var actual = repository().findByReserverId(2);
        assertThat(actual).hasSize(1);

        // 2件ヒットパターン
        actual = repository().findByReserverId(1);
        assertThat(actual).hasSize(2);

        // 0件ヒットパターン
        actual = repository().findByReserverId(3);
        assertThat(actual).isEmpty();
    }

    @Test
    void testFindOverlappedReservation() {

        // ---------
        // NOTE:利用の開始/終了日時についてはまったく同一でなくても時間帯が重複していればよしとする。
        // ---------

        // 指定開始時間が既存予約時間帯に完全に含まれる
        var expect = Reservation.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1);
        var actual = repository().findOverlappedReservation(3, LocalDateTime.of(2020, 4, 1, 11, 00), LocalDateTime.of(2020, 4, 1, 14, 00));
        assertThatToString(actual).isEqualTo(expect);


        //指定終了時間が既存予約時間帯に含まれるケース
        actual = repository().findOverlappedReservation(3, LocalDateTime.of(2020, 4, 1, 9, 00), LocalDateTime.of(2020, 4, 1, 11, 00));
        assertThatToString(actual).isEqualTo(expect);


        //指定時間帯が既存の予約時間帯を包含するケース
        actual = repository().findOverlappedReservation(3, LocalDateTime.of(2020, 4, 1, 9, 00), LocalDateTime.of(2020, 4, 1, 14, 00));
        assertThatToString(actual).isEqualTo(expect);


        //指定時間帯が既存の予約時間帯に包含されるケース
        actual = repository().findOverlappedReservation(3, LocalDateTime.of(2020, 4, 1, 10, 30), LocalDateTime.of(2020, 4, 1, 11, 30));
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testFindNullOverlappedReservation() {
        //時間帯が重複しないケース
        var actual = repository().findOverlappedReservation(3, LocalDateTime.of(2999, 4, 1, 13, 0), LocalDateTime.of(2999, 4, 1, 15, 30));
        assertThat(actual).isNull();
    }

    @Test
    @TransactionalForTest
    void testDelete() {
        // 削除実行
        var deleteReservation = repository().get(2);
        repository().delete(deleteReservation);

        // 削除後ファイルの取得
        var reservations = this.repository().findAll();

        // 検証
        var expected = List.of(
                Reservation.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1),
                Reservation.of(3, LocalDateTime.of(2021, 4, 1, 10, 0, 0), LocalDateTime.of(2021, 4, 1, 12, 0, 0), "メモ3", 3, 1)
                );
        assertThatToString(reservations).containsExactlyElementsOf(expected);
    }
}
