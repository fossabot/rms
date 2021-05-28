package com.mamezou.rms.external.webapi.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.mamezou.rms.core.domain.RentalItem;
import com.mamezou.rms.core.domain.constraint.ItemName;
import com.mamezou.rms.core.domain.constraint.SerialNo;

import lombok.Getter;
import lombok.Setter;

@Schema(description = "レンタル品登録用DTO")
@Getter
@Setter
public class AddRentalItemDto {

    @SerialNo
    @Schema(required = true)
    private String serialNo;

    @ItemName
    @Schema(required = false)
    private String itemName;

    public RentalItem toEntity() {
        return RentalItem.ofTransient(serialNo, itemName);
    }
}
