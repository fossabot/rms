package com.mamezou.rms.core.persistence.file;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.mamezou.rms.core.domain.RentalItem;
import com.mamezou.rms.core.persistence.RentalItemRepository;
import com.mamezou.rms.core.persistence.GenericRepository.ApiType;
import com.mamezou.rms.core.persistence.file.converter.EntityArrayConverter;
import com.mamezou.rms.core.persistence.file.io.FileAccessor;
import com.mamezou.rms.platform.extension.EnabledIfRuntimeConfig;

@ApplicationScoped
@EnabledIfRuntimeConfig(propertyName = ApiType.PROP_NAME, value = ApiType.FILE)
public class RentalItemFileRepository extends AbstractFileRepository<RentalItem> implements RentalItemRepository {

    @Inject
    public RentalItemFileRepository(FileAccessor fileAccessor, EntityArrayConverter<RentalItem> converter) {
        super(fileAccessor, converter);
    }

    @Override
    public RentalItem findBySerialNo(String serialNo) {
        return this.load().stream()
                .filter(attributes -> attributes[1].equals(serialNo))
                .map(this.getConverter()::toEntity)
                .findFirst()
                .orElse(null);
    }
}
