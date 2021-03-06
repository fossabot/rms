package com.mamezou.rms.external.webapi.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.mamezou.rms.core.domain.UserAccount;
import com.mamezou.rms.core.domain.UserAccount.UserType;

import lombok.Getter;
import lombok.Setter;

@Schema(description = "ユーザDTO")
@Getter
@Setter
public class UserAccountResourceDto {

    @Schema(required = true)
    private Integer id;

    @Schema(required = true)
    private String loginId;

    @Schema(required = true)
    private String password;

    @Schema(required = false)
    private String userName;

    @Schema(required = false)
    private String phoneNumber;

    @Schema(required = false)
    private String contact;

    @Schema(required = true)
    private UserType userType;

    public static UserAccountResourceDto toDto(UserAccount entity) {
        if (entity == null) {
            return null;
        }
        var dto = new UserAccountResourceDto();
        dto.setId(entity.getId());
        dto.setLoginId(entity.getLoginId());
        dto.setPassword(entity.getPassword());
        dto.setUserName(entity.getUserName());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setContact(entity.getContact());
        dto.setUserType(entity.getUserType().name());
        return dto;
    }

    public UserAccount toEntity() {
        return UserAccount.of(id, loginId, password, userName, phoneNumber, contact, userType);
    }

    // original getter
    public String getUserType() {
        return userType.name();
    }

    // original setter
    public void setUserType(String userType) {
        this.userType = UserType.valueOf(userType);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
