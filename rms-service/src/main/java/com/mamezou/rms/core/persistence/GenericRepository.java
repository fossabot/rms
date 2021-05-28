package com.mamezou.rms.core.persistence;

import java.util.List;

import javax.validation.Valid;

import com.mamezou.rms.platform.validate.ValidateParam;

/**
 * 永続先に依らないリポジトリの共通操作
 *
 * @param <T> エンティティの型
 */
public interface GenericRepository<T> {

    /**
     * IDのエンティティを取得する。
     *
     * @param id ID
     * @return エンティティ。該当なしはnull
     */
    T get(int id);

    /**
     * 永続化されているエンティティを全件取得する
     *
     * @return エンティティの全件リスト。該当なしは空リスト
     */
    List<T> findAll();

    /**
     * エンティティを追加する。
     * 実装クラスもしくはメソッドに{@link ValidateParam}がアノテートすることでメソッド実行前に
     * {@link Valid}によりオブジェクトのValidationが実行される。
     *
     * @param entity エンティティ
     */
    void add(T entity);

    /**
     * コンフィグ定数
     */
    static class ApiType {
        public static final String PROP_NAME ="persistence.apiType";
        public static final String FILE = "file";
        public static final String JPA = "jpa";
    }
}
