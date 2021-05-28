package com.mamezou.rms.core.integration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.extension.ExtendWith;

import com.mamezou.rms.core.persistence.file.IoSystemException;
import com.mamezou.rms.test.junit5.JulToSLF4DelegateExtension;

import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest
@AddConfig(key = "persistence.apiType", value = "file")
@AddConfig(key = "csv.type", value = "permanent")
@AddConfig(key = "csv.permanent.directory", value = IntegrationScenarioByPermanentFileTest.TEST_PERMANENT_DIR)
@ExtendWith(JulToSLF4DelegateExtension.class)
class IntegrationScenarioByPermanentFileTest extends AbstractRentalReservationIntegrationScenario {

    public static final String TEST_PERMANENT_DIR = "./target/temp-integrationtest";

    @AfterAll
    static void teardownAfterAll() throws Exception {
        var targetDir = Paths.get(TEST_PERMANENT_DIR);
        Files.list(targetDir).forEach(IntegrationScenarioByPermanentFileTest::deleteQuietly); // ファイル削除
        Files.deleteIfExists(targetDir); // ディレクトリ削除
    }

    static boolean deleteQuietly(Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new IoSystemException(e);
        }
    }

    @Override
    protected int expectedReregistrationId() {
        return 4; // Fileはその時点のレコードのmax(reservation.id)+1となる
    }
}
