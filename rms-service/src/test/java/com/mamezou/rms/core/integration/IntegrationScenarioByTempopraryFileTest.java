package com.mamezou.rms.core.integration;

import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;

import com.mamezou.rms.test.junit5.JulToSLF4DelegateExtension;

import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@DisabledIfEnvironmentVariable(named = "RMS_CI_ENV", matches = "github")
@HelidonTest
@AddConfig(key = "persistence.apiType", value = "file")
@AddConfig(key = "csv.type", value = "temporary")
@ExtendWith(JulToSLF4DelegateExtension.class)
class IntegrationScenarioByTempopraryFileTest extends AbstractRentalReservationIntegrationScenario {

    @Override
    protected int expectedReregistrationId() {
        return 4; // Fileはその時点のレコードのmax(reservation.id)+1となる
    }
}
