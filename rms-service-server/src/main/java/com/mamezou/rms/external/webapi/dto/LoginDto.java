package com.mamezou.rms.external.webapi.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.mamezou.rms.core.domain.constraint.LoginId;
import com.mamezou.rms.core.domain.constraint.Passowrd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "ログインDTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class LoginDto {

    @LoginId
    @Schema(required = true, minLength = 5, maxLength = 10)
    private String loginId;

    @Passowrd
    @Schema(required = true, minLength = 5, maxLength = 10)
    private String password;
}
