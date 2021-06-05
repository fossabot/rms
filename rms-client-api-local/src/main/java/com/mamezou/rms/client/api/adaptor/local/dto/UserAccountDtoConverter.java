package com.mamezou.rms.client.api.adaptor.local.dto;

import com.mamezou.rms.client.api.dto.UserAccountClientDto;
import com.mamezou.rms.client.api.dto.UserAccountClientDto.ClientUserType;
import com.mamezou.rms.core.domain.UserAccount;
import com.mamezou.rms.core.domain.UserAccount.UserType;

public class UserAccountDtoConverter {

    public static UserAccountClientDto toDto(UserAccount user) {
        var dto = new UserAccountClientDto();
        dto.setId(user.getId());
        dto.setLoginId(user.getLoginId());
        dto.setPassword(user.getPassword());
        dto.setUserName(user.getUserName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setContact(user.getContact());
        dto.setUserType(ClientUserType.valueOf(user.getUserType().name()));
        return dto;
    }

    public static UserAccount toEntity(UserAccountClientDto dto) {
        var user = new UserAccount();
        user.setId(dto.getId());
        user.setLoginId(dto.getLoginId());
        user.setPassword(dto.getPassword());
        user.setUserName(dto.getUserName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setContact(dto.getContact());
        user.setUserType(UserType.valueOf(dto.getUserType().name()));
        return user;
    }
}
