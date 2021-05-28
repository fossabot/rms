package com.mamezou.rms.core.persistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.mamezou.rms.core.domain.Reservation;

/**
 * 予約の永続化インタフェース。
 */
public interface ReservationRepository extends GenericRepository<Reservation> {

    /**
     * レンタル品IDと利用開始日が一致する予約一覧を取得する。
     *
     * @param rentalItemId レンタル品ID
     * @param startDate 利用開始日
     * @return 該当予約。該当がない場合は空リスト
     */
    List<Reservation> findByRentalItemAndStartDate(int rentalItemId, LocalDate startDate);

    /**
     * 指定されたユーザIDが予約者の予約一覧を取得する。
     * @param reserverId 予約者のユーザID
     * @return 該当予約。該当がない場合は空リスト
     */
    List<Reservation> findByReserverId(int reserverId);

    /**
     * 指定されたレンタル品の予約のうち、利用開始日時～利用終了日時の間に利用時間が重なっている予約を取得する
     *
     * @param rentalItemId レンタル品ID
     * @param startDateTime 利用開始日時
     * @param endDateTime 仕様終了日時
     * @return 予約。該当なしはnull
     */
    Reservation findOverlappedReservation(int rentalItemId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 引数で受け取った予約を削除します。
     *
     * @param reservation 削除する予約
     */
    void delete(Reservation reservation);
}