package com.mamezou.rms.external.webapi.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.mamezou.rms.core.domain.RentalItem;

import lombok.Getter;
import lombok.Setter;

@Schema(description = "レンタル品DTO")
@Getter
@Setter
public class RentalItemResourceDto {

    @Schema(required = true)
    private Integer id;

    @Schema(required = true)
    private String serialNo;

    @Schema(required = false)
    private String itemName;

    public static RentalItemResourceDto toDto(RentalItem entity) {
        if (entity == null) {
            return null;
        }
        var dto = new RentalItemResourceDto();
        dto.setId(entity.getId());
        dto.setSerialNo(entity.getSerialNo());
        dto.setItemName(entity.getItemName());
        return dto;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
