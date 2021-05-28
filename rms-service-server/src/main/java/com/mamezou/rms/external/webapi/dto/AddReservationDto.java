package com.mamezou.rms.external.webapi.dto;

import java.time.LocalDateTime;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.mamezou.rms.core.domain.Reservation;
import com.mamezou.rms.core.domain.constraint.BeforeAfterDateTime;
import com.mamezou.rms.core.domain.constraint.Note;
import com.mamezou.rms.core.domain.constraint.ReserveEndDateTime;
import com.mamezou.rms.core.domain.constraint.ReserveStartDateTimeFuture;
import com.mamezou.rms.core.domain.constraint.RmsId;
import com.mamezou.rms.core.domain.constraint.BeforeAfterDateTime.BeforeAfterDateTimeValidatable;
import com.mamezou.rms.core.domain.constraint.ValidationGroups.Add;

import lombok.Getter;
import lombok.Setter;

@Schema(description = "予約登録用DTO")
@BeforeAfterDateTime
@Getter
@Setter
public class AddReservationDto implements BeforeAfterDateTimeValidatable {

    @Schema(required = true, ref = "#/components/schemas/localDateTime")
    @ReserveStartDateTimeFuture(groups = Add.class)
    private LocalDateTime startDateTime;

    @Schema(required = true, ref = "#/components/schemas/localDateTime")
    @ReserveEndDateTime
    private LocalDateTime endDateTime;

    @Schema(required = false)
    @Note
    private String note;

    @RmsId
    @Schema(required = true)
    private int rentalItemId;

    @RmsId
    @Schema(required = true)
    private int userAccountId;

    public Reservation toEntity() {
        return Reservation.ofTransient(startDateTime, endDateTime, note, rentalItemId, userAccountId);
    }
}
