package com.mamezou.rms.core.persistence;

import com.mamezou.rms.core.domain.UserAccount;

public interface UserAccountRepository extends GenericRepository<UserAccount> {

    /**
     * ログインIDとパスワードに一致するユーザを取得。
     *
     * @param loginId ログインID
     * @param password パスワード
     * @return 該当ユーザ。該当なしはnull
     */
    UserAccount findByLoginIdAndPasswod(String loginId, String password);

    //
    /**
     * ログインIDに一致するユーザを取得する。
     *
     * @param loginId ログインID
     * @return 該当ユーザ。該当なしはnull
     */
    UserAccount findByLoginId(String loginId);

    /**
     * ユーザを更新する
     *
     * @param entity 更新内容
     * @return 更新後エンティティ。更新対象が存在しない場合はnull
     */
    UserAccount update(UserAccount entity);
}