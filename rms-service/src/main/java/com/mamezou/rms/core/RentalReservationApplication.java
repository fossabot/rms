package com.mamezou.rms.core;

import java.time.LocalDate;
import java.util.List;

import com.mamezou.rms.core.domain.RentalItem;
import com.mamezou.rms.core.domain.Reservation;
import com.mamezou.rms.core.domain.UserAccount;
import com.mamezou.rms.core.exception.BusinessFlowException;

/**
 * レンタル予約アプリケーションインターフェース
 */
public interface RentalReservationApplication {

    /**
     * ユーザをパスワードで認証する。
     * <p>
     * @param loginId 認証するユーザのログインID
     * @param password 認証パスワード
     * @return 認証ユーザ。認証できなかった場合はnull
     * @throws BusinessFlowException ユーザIDまたはパスワードに一致するユーザがいない
     */
    UserAccount authenticate(String loginId, String password) throws BusinessFlowException;

    /**
     * 指定されたIDのエンティティを取得する。
     * <p>
     * @param <T> エンティティクラス
     * @param entityClass 取得するエンティティクラス
     * @param id ID
     * @return エンティティ
     */
    <T> T get(Class<T> entityClass, int id);

    /**
     * 指定されたレンタル品と利用開始日に対する予約を取得する。
     * <p>
     * @param rentalItemId 予約のレンタル品ID
     * @param startDate 予約の利用開始日
     * @return 予約リスト（該当なしは例外を送出）
     * @throws BusinessFlowException 該当なし
     */
    List<Reservation> findReservationByRentalItemAndStartDate(Integer rentalItemId, LocalDate startDate) throws BusinessFlowException;

    /**
     * 指定されたユーザが予約者の予約を取得する。
     *
     * @param reserverId 予約者のユーザID
     * @return 該当のリスト。該当なしは空リスト
     */
    List<Reservation> findReservationByReserverId(int reserverId);

    /**
     * レンタル品の全件取得。
     * <p>
     * @return レンタル品の全件。該当なしは空リスト
     */
    List<RentalItem> getAllRetailItems();

    /**
     * ユーザの全件取得。
     * <p>
     * @return ユーザの全件。該当なしは空リスト
     */
    List<UserAccount> getAllUserAccounts();

    /**
     * レンタル品を予約する。
     * <p>
     * @param addReservation 登録する予約（idはnull）
     * @return 登録された予約（idが設定されている）
     * @throws BusinessFlowException 該当するレンタル品が存在しない場合、または期間が重複する予約が既に登録されている場合
     */
    Reservation addReservation(Reservation addReservation) throws BusinessFlowException;

    /**
     * レンタル品を登録する。
     * <p>
     * @param addRentalItem 登録レンタル品（idはnull）
     * @return 登録されたレンタル品（idが設定されている）
     * @throws BusinessFlowException 同一シリアル番号のレンタル品が既に登録されている場合
     */
    RentalItem addRentalItem(RentalItem addRentalItem) throws BusinessFlowException;

    /**
     * ユーザアカウントを登録する。
     * <p>
     * @param addUserAccount 登録ユーザ（idはnull）
     * @return 登録されたユーザアカウント（idが設定されている）
     * @throws BusinessFlowException 同一ログインIDのユーザが既に登録されている場合
     */
    UserAccount addUserAccount(UserAccount addUserAccount) throws BusinessFlowException;

    /**
     * 予約をキャンセルする。
     * <p>
     * @param reservationId 予約ID
     * @throws BusinessFlowException 該当の予約が存在しない場合。予約者以外が取消を行っている場合
     */
    void cancelReservation(int reservationId) throws BusinessFlowException;

    /**
     * ユーザアカウントを更新する。
     * <p>
     * @param updateUserAccount 更新ユーザ
     * @return 更新されたユーザアカウント。更新対象がない場合はnull
     */
    UserAccount  updateUserAccount(UserAccount updateUserAccount);
}