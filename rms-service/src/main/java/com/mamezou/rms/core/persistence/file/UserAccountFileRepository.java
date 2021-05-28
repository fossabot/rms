package com.mamezou.rms.core.persistence.file;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;

import com.mamezou.rms.core.domain.UserAccount;
import com.mamezou.rms.core.domain.constraint.ValidationGroups.Update;
import com.mamezou.rms.core.persistence.UserAccountRepository;
import com.mamezou.rms.core.persistence.GenericRepository.ApiType;
import com.mamezou.rms.core.persistence.file.converter.EntityArrayConverter;
import com.mamezou.rms.core.persistence.file.io.FileAccessor;
import com.mamezou.rms.platform.extension.EnabledIfRuntimeConfig;
import com.mamezou.rms.platform.validate.ValidateGroup;
import com.mamezou.rms.platform.validate.ValidateParam;

@ApplicationScoped
@EnabledIfRuntimeConfig(propertyName = ApiType.PROP_NAME, value = ApiType.FILE)
public class UserAccountFileRepository extends AbstractFileRepository<UserAccount> implements UserAccountRepository {

    @Inject
    public UserAccountFileRepository(FileAccessor fileAccessor, EntityArrayConverter<UserAccount> converter) {
        super(fileAccessor, converter);
    }

    @Override
    public UserAccount findByLoginIdAndPasswod(String loginId, String password) {
        return load().stream()
                .filter(attributes -> attributes[1].equals(loginId))
                .filter(attributes -> attributes[2].equals(password))
                .map(this.getConverter()::toEntity)
                .findFirst()
                .orElse(null);
    }

    @Override
    public UserAccount findByLoginId(String loginId) {
        return load().stream()
                .filter(attributes -> attributes[1].equals(loginId))
                .map(this.getConverter()::toEntity)
                .findFirst()
                .orElse(null);
    }

    @ValidateParam
    @ValidateGroup(groups = Update.class)
    @Override
    public UserAccount update(@Valid UserAccount updateUser) {
        var replaced = new AtomicBoolean(false);
        var lines = load().stream()
                .map(items -> {
                    if (items[0].equals(String.valueOf(updateUser.getId()))) {
                        replaced.set(true);
                        return getConverter().toArray(updateUser);
                    }
                    return items;
                })
                .collect(Collectors.toList());
        if (!replaced.get()) {
            return null;
        }
        this.saveAll(lines);
        return updateUser;
    }
}
