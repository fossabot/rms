package com.mamezou.rms.core.domain;

import static java.time.temporal.ChronoUnit.*;
import static javax.persistence.AccessType.*;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;

import javax.persistence.Access;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.mamezou.rms.core.domain.constraint.BeforeAfterDateTime;
import com.mamezou.rms.core.domain.constraint.Note;
import com.mamezou.rms.core.domain.constraint.ReserveEndDateTime;
import com.mamezou.rms.core.domain.constraint.ReserveStartDateTime;
import com.mamezou.rms.core.domain.constraint.ReserveStartDateTimeFuture;
import com.mamezou.rms.core.domain.constraint.RmsId;
import com.mamezou.rms.core.domain.constraint.BeforeAfterDateTime.BeforeAfterDateTimeValidatable;
import com.mamezou.rms.core.domain.constraint.ValidationGroups.Add;
import com.mamezou.rms.core.domain.constraint.ValidationGroups.Update;

import lombok.Getter;
import lombok.Setter;

@Access(FIELD)
@Entity
@BeforeAfterDateTime(from = "利用開始日時", to = "利用終了日時")
@Getter
@Setter
public class Reservation implements BeforeAfterDateTimeValidatable, Transformable, IdAccessable {

    /** id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RmsId(groups = Update.class)
    private Integer id;

    /** 利用開始日時 */
    @ReserveStartDateTime
    @ReserveStartDateTimeFuture(groups = Add.class)
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime startDateTime;

    /** 利用終了日時 */
    @ReserveEndDateTime
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime endDateTime;

    /** メモ */
    @Note
    private String note;

    /** 予約したレンタル品ID */
    @RmsId
    private int rentalItemId;

    /** 予約したユーザのユーザアカウントID */
    @RmsId
    private int userAccountId;

    /** 予約したユーザ */
    private transient UserAccount userAccount;

    /** 予約したレンタル品 */
    private transient RentalItem rentalItem;


    // ----------------------------------------------------- factory methods

    public static Reservation of(Integer reservationId, LocalDateTime startDateTime, LocalDateTime endDateTime, String note,
            int rentalItemId, int userAccountId) {

        var entity = new Reservation();
        entity.id = reservationId;
        entity.startDateTime = startDateTime;
        entity.endDateTime = endDateTime;
        entity.note = note;
        entity.rentalItemId = rentalItemId;
        entity.userAccountId = userAccountId;
        return entity;
    }

    public static Reservation ofTransient(LocalDateTime startDateTime, LocalDateTime endDateTime, String note, int rentalItemId, int userAccountId) {
        return of(null, startDateTime, endDateTime, note, rentalItemId, userAccountId);
    }

    // ----------------------------------------------------- original setter methods

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime == null ? null : startDateTime.truncatedTo(MINUTES);
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime == null ? null : endDateTime.truncatedTo(MINUTES);
    }


    // ----------------------------------------------------- service methods

    public DateTimePeriod getReservePeriod() {
        return new DateTimePeriod(startDateTime, endDateTime);
    }


    // ----------------------------------------------------- override methods

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }


    // ----------------------------------------------------- inner classes

    public static class DateTimePeriod {

        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private Range<ChronoLocalDateTime<?>> period;

        public DateTimePeriod(LocalDateTime startDateTime, LocalDateTime endDateTime) {
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            period = Range.between(startDateTime, endDateTime);        }

        public LocalDateTime getStartDateTime() {
            return this.startDateTime;
        }

        public LocalDateTime getEndDateTime() {
            return this.endDateTime;
        }

        public boolean isOverlappedBy(DateTimePeriod otherPeriod) {
            return this.period.isOverlappedBy(otherPeriod.period);
        }
    }
}
