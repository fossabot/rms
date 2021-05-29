package com.mamezou.rms.core.integration;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;

import com.mamezou.rms.test.junit5.JulToSLF4DelegateExtension;

import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@Disabled
@HelidonTest
@AddConfig(key = "persistence.apiType", value = "jpa")
@ExtendWith(JulToSLF4DelegateExtension.class)
class IntegrationScenarioByJpaTest extends AbstractRentalReservationIntegrationScenario {

    @Override
    protected int expectedReregistrationId() {
        return 5; // DBはシーケンスなので、その時点のレコードのmax(reservation.id)とならない
    }
}
