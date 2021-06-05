package com.mamezou.rms.core.persistence.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.eclipse.microprofile.config.Config;

import com.mamezou.rms.core.domain.RentalItem;
import com.mamezou.rms.core.domain.Reservation;
import com.mamezou.rms.core.domain.UserAccount;
import com.mamezou.rms.core.persistence.file.converter.EntityArrayConverter;
import com.mamezou.rms.core.persistence.file.converter.RentalItemArrayConverter;
import com.mamezou.rms.core.persistence.file.converter.ReservationArrayConverter;
import com.mamezou.rms.core.persistence.file.converter.UserAccountArrayConverter;
import com.mamezou.rms.core.persistence.file.io.FileAccessor;
import com.mamezou.rms.core.persistence.file.io.PathResolver;

import lombok.extern.slf4j.Slf4j;

@Dependent
@Slf4j
public class FileRepositoryInitializeProducers {

    // config value is "permanent" or "temporary"
    private static final String FILE_TYPE_CONFIG_KEY = "csv.type";
    // samaple key is "csv-file.permanent.file_name.reservation = reservation.csv"
    private static final String FILE_NAME_CONFIG_KEY_FORMAT = "csv.%s.fileName.%s";

    private static final Map<Class<?>, String> FILE_NAME_TYPE_CONFIG_KEY_MAP = Map.of(
                RentalItemFileRepository.class, "rentalitem",
                ReservationFileRepository.class, "reservation",
                UserAccountFileRepository.class, "useraccount"
            );

    @Inject
    private Config config;

    @Produces
    public FileAccessor creteFileAccessor(InjectionPoint injectionPoint) throws IOException {

        // 1%sの文字列取得
        var fileType = config.getValue(FILE_TYPE_CONFIG_KEY, String.class);

        // 2%sの文字列取得
        Class<?> beanClass = injectionPoint.getBean().getBeanClass();
        var fileNameTypeConfigKey = FILE_NAME_TYPE_CONFIG_KEY_MAP.get(beanClass);

        // ファイル名のConfigKeyの決定
        var fileNameConfigKey = String.format(FILE_NAME_CONFIG_KEY_FORMAT, fileType, fileNameTypeConfigKey);
        var fileName = config.getValue(fileNameConfigKey, String.class);

        // フィルパスの取得
        Path filePath;
        switch (fileType) {
            case "permanent":
                filePath = new PathResolver.FixedDirPathResolver().resolve(fileName);
                break;
            case "temporary":
                filePath =  FileAccessor.copyResourceToRealPath(fileName, new PathResolver.TempDirPathResolver());
                break;
            default:
                throw new IllegalArgumentException("unknown fileType -> " + fileType);
        }
        log.info("[{}]モードでファイルをオープンしました。PATH={}", fileType, filePath);

        return new FileAccessor(filePath);
    }

    @Produces
    public EntityArrayConverter<RentalItem> createRentalItemConverter(InjectionPoint injectionPoint) {
        return RentalItemArrayConverter.INSTANCE;
    }

    @Produces
    public EntityArrayConverter<Reservation> createReservationConverter(InjectionPoint injectionPoint) {
        return ReservationArrayConverter.INSTANCE;
    }

    @Produces
    public EntityArrayConverter<UserAccount> createUserAccoutConverter(InjectionPoint injectionPoint) {
        return UserAccountArrayConverter.INSTANCE;
    }
}
