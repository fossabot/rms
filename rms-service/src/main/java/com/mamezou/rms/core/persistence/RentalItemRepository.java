package com.mamezou.rms.core.persistence;

import java.util.List;

import com.mamezou.rms.core.domain.RentalItem;

/**
 * レンタル品の永続化インタフェース。
 */
public interface RentalItemRepository extends GenericRepository<RentalItem> {

    /**
     * レンタル品の全件取得。
     *
     * @return 全件。該当がない場合は空リスト
     */
    List<RentalItem> findAll();

    //
    /**
     * シリアル番号を指定してレンタル品を取得。
     *
     * @param serialNo シリアル番号
     * @return 該当エンティティ。該当なしはnull
     */
    RentalItem findBySerialNo(String serialNo);
}