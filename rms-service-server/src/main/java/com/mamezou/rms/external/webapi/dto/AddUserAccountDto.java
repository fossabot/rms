package com.mamezou.rms.external.webapi.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.mamezou.rms.core.domain.UserAccount;
import com.mamezou.rms.core.domain.UserAccount.UserType;
import com.mamezou.rms.core.domain.constraint.ItemName;
import com.mamezou.rms.core.domain.constraint.LoginId;
import com.mamezou.rms.core.domain.constraint.Passowrd;
import com.mamezou.rms.core.domain.constraint.UserName;
import com.mamezou.rms.core.domain.constraint.UserTypeConstraint;

import lombok.Getter;
import lombok.Setter;

@Schema(description = "ユーザ登録用DTO")
@Getter
@Setter
public class AddUserAccountDto {

    @Schema(required = true)
    @LoginId
    private String loginId;

    @Schema(required = true)
    @Passowrd
    private String password;

    @Schema(required = true)
    @UserName
    private String userName;

    @Schema(required = false)
    @ItemName
    private String phoneNumber;

    @Schema(required = false)
    private String contact;

    @Schema(required = true)
    @UserTypeConstraint
    private UserType userType;

    public UserAccount toEntity() {
        return UserAccount.ofTransient(loginId, password, userName, phoneNumber, contact, userType);
    }
}
