package com.mamezou.rms.external.webapi.dto;

import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.mamezou.rms.core.domain.Reservation;

import lombok.Getter;
import lombok.Setter;

@Schema(description = "予約DTO")
@Getter
@Setter
public class ReservationResourceDto {

    @Schema(required = true)
    private Integer id;

    @Schema(required = true)
    private LocalDateTime startDateTime;

    @Schema(required = true)
    private LocalDateTime endDateTime;

    @Schema(required = false)
    private String note;

    @Schema(required = true)
    private int rentalItemId;

    @Schema(required = true)
    private int userAccountId;

    @Schema(required = true)
    private UserAccountResourceDto userAccountDto;

    @Schema(required = true)
    private RentalItemResourceDto rentalItemDto;

    public static ReservationResourceDto toDto(Reservation entity) {
        if (entity == null) {
            return null;
        }
        var dto = new ReservationResourceDto();
        dto.setId(entity.getId());
        dto.setStartDateTime(entity.getStartDateTime());
        dto.setEndDateTime(entity.getEndDateTime());
        dto.setNote(entity.getNote());
        dto.setRentalItemId(entity.getRentalItemId());
        dto.setUserAccountId(entity.getUserAccountId());
        dto.setRentalItemDto(entity.getRentalItem().transform(RentalItemResourceDto::toDto));
        dto.setUserAccountDto(entity.getUserAccount().transform(UserAccountResourceDto::toDto));
        return dto;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
