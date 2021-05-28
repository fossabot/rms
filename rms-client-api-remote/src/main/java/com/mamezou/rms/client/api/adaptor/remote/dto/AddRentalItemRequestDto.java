package com.mamezou.rms.client.api.adaptor.remote.dto;

import com.mamezou.rms.client.api.dto.RentalItemClientDto;

import lombok.Getter;

@Getter
public class AddRentalItemRequestDto {

    private String serialNo;
    private String itemName;

    // ----------------------------------------------------- constructor methods

    public AddRentalItemRequestDto(RentalItemClientDto clientDto) {
        serialNo = clientDto.getSerialNo();
        itemName = clientDto.getItemName();
    }
}
