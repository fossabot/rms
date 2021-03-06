package com.mamezou.rms.core.domain.constraint;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

/**
 * 予約開始日時チェックアノテーション。
 * 登録時にのみ利用する
 * <pre>
 * ・nullでないこと
 * ・現在日時より未来であること
 * </pre>
 */
@Documented
@Constraint(validatedBy = {})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@NotNull
@Future
public @interface ReserveStartDateTimeFuture {
    String message() default "{com.mamezou.rms.service.contrains.Generic.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        ReserveStartDateTimeFuture[] value();
    }
}
