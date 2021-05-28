package com.mamezou.rms.core.persistence.file.converter;

import com.mamezou.rms.core.domain.UserAccount;
import com.mamezou.rms.core.domain.UserAccount.UserType;

public class UserAccountArrayConverter implements EntityArrayConverter<UserAccount> {

    public static final UserAccountArrayConverter INSTANCE = new UserAccountArrayConverter();

    public UserAccount toEntity(String[] attributes) {

        int id = Integer.parseInt(attributes[0]);
        String loginId = attributes[1];
        String password = attributes[2];
        String userName = attributes[3];
        String phoneNumber = attributes[4];
        String contact = attributes[5];
        UserType userType = UserType.valueOf(attributes[6]);

        return UserAccount.of(id, loginId, password, userName, phoneNumber, contact, userType);
    }

    public String[] toArray(UserAccount userAccount) {

        String[] userAccountAttributes = new String[7];

        userAccountAttributes[0] = String.valueOf(userAccount.getId());
        userAccountAttributes[1] = userAccount.getLoginId();
        userAccountAttributes[2] = userAccount.getPassword();
        userAccountAttributes[3] = userAccount.getUserName();
        userAccountAttributes[4] = userAccount.getPhoneNumber();
        userAccountAttributes[5] = userAccount.getContact();
        userAccountAttributes[6] = userAccount.getUserType().name();

        return userAccountAttributes;
    }
}
