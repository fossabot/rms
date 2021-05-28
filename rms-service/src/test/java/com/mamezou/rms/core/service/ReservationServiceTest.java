package com.mamezou.rms.core.service;

import static com.mamezou.rms.core.TestUtils.*;
import static com.mamezou.rms.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.mamezou.rms.core.TestUtils.PathResolverParameterExtension;
import com.mamezou.rms.core.domain.Reservation;
import com.mamezou.rms.core.exception.BusinessFlowException;
import com.mamezou.rms.core.exception.BusinessFlowException.CauseType;
import com.mamezou.rms.core.persistence.file.io.PathResolver;

@ExtendWith(PathResolverParameterExtension.class)
class ReservationServiceTest {

    private ReservationService service;

    @BeforeEach
    void setup(PathResolver pathResolver) throws Exception {
        service = newReservationService(pathResolver);
    }

    @Test
    void testFindReservationByRentalItemAndStartDate() {
        var expect = List.of(
                Reservation.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1),
                Reservation.of(2, LocalDateTime.of(2020, 4, 1, 16, 0, 0), LocalDateTime.of(2020, 4, 1, 18, 0, 0), "メモ2", 3, 2)
            );
        var actual = service.findByRentalItemAndStartDate(3, LocalDate.of(2020, 4, 1));
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }

    @Test
    void testFindEmptyReservationByRentalItemAndStartDate() {
        var actual = service.findByRentalItemAndStartDate(903, LocalDate.of(2020, 4, 1)); // rentalItemId:903が存在しない
        assertThat(actual).isEmpty();

        actual = service.findByRentalItemAndStartDate(1, LocalDate.of(2020, 7, 10)); // 2020/7/10の予約はない
        assertThat(actual).isEmpty();

        actual = service.findByRentalItemAndStartDate(903, LocalDate.of(2020, 7, 10)); // 上記の両方
        assertThat(actual).isEmpty();
    }

    @Test
    void testFindOverlappedReservation() {
        var expect = Reservation.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1);
        var actual = service.findOverlappedReservation(3, LocalDateTime.of(2020, 4, 1, 9, 0, 0), LocalDateTime.of(2020, 4, 1, 13, 0, 0));
        assertThatToString(actual).isEqualTo(expect); // 2020/4/1 10:00-12:00の予約と重複
    }

    @Test
    void testFindOverlappedReservationNull() {
        var actual = service.findOverlappedReservation(1, LocalDateTime.of(2020, 4, 1, 13, 0, 0), LocalDateTime.of(2020, 4, 1, 15, 0, 0));
        assertThat(actual).isNull(); // 期間重複の予約なし

        actual = service.findOverlappedReservation(903, LocalDateTime.of(2020, 4, 1, 11, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0));
        assertThat(actual).isNull(); // 期間重複の予約なし(該当レンタル品なし)
    }

    @Test
    void testFindReservationByReserverId() {
        // 1件ヒットパターン
        var actual = service.findByReserverId(2);
        assertThat(actual).hasSize(1);
        // 2件ヒットパターン
        actual = service.findByReserverId(1);
        assertThat(actual).hasSize(2);
        // 0件ヒットパターン
        actual = service.findByReserverId(3);
        assertThat(actual).isEmpty();
    }


    @Test
    void testGet() {
        var expect = Reservation.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1);
        var actual = service.get(1);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testGetNull() {
        var actual = service.get(100);
        assertThat(actual).isNull(); // 該当IDの予約なし
    }

    @Test
    void testAdd() {
        var addEntity = Reservation.ofTransient(LocalDateTime.of(2020, 4, 2, 10, 0, 0), LocalDateTime.of(2020, 4, 2, 12, 0, 0), "メモ4", 3, 1);
        var expect = Reservation.of(4, LocalDateTime.of(2020, 4, 2, 10, 0, 0), LocalDateTime.of(2020, 4, 2, 12, 0, 0), "メモ4", 3, 1);
        var actual = service.add(addEntity);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testCancel() {
        service.cancel(2, 2);
        var actual = service.get(2);
        assertThat(actual).isNull();
    }

    @Test
    void testCancelTargetNotFound() {
        BusinessFlowException actual = catchThrowableOfType(() ->
            service.cancel(4, 2),
            BusinessFlowException.class
        );
        assertThat(actual.getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    void testCancelOperationForbidden() {
        BusinessFlowException actual = catchThrowableOfType(() ->
            service.cancel(2, 1),
            BusinessFlowException.class
        );
        assertThat(actual.getCauseType()).isEqualTo(CauseType.FORBIDDEN);
    }
}
