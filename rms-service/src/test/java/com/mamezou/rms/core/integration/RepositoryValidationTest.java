package com.mamezou.rms.core.integration;

import static org.assertj.core.api.Assertions.*;

import javax.enterprise.inject.spi.CDI;
import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.mamezou.rms.core.domain.RentalItem;
import com.mamezou.rms.core.domain.Reservation;
import com.mamezou.rms.core.domain.UserAccount;
import com.mamezou.rms.core.persistence.GenericRepository;
import com.mamezou.rms.core.persistence.RentalItemRepository;
import com.mamezou.rms.core.persistence.ReservationRepository;
import com.mamezou.rms.core.persistence.UserAccountRepository;
import com.mamezou.rms.test.assertj.ConstraintViolationSetAssert;
import com.mamezou.rms.test.junit5.JulToSLF4DelegateExtension;

import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@Disabled
@ExtendWith(JulToSLF4DelegateExtension.class)
public class RepositoryValidationTest {

    @Nested
    @HelidonTest(resetPerTest = true)
    @AddConfig(key = "persistence.apiType", value = "file")
    @AddConfig(key = "csv.type", value = "temporary")
    @ExtendWith(JulToSLF4DelegateExtension.class)
    static class FileRepositoryValidationTest {

        @Test
        void testAddValidate() {
            testAddEntity();
        }

        @Test
        void testUpdateValidate() {
            testUpdateEntity();
        }
    }

    @Nested
    @HelidonTest(resetPerTest = true)
    @AddConfig(key = "persistence.apiType", value = "jpa")
    @ExtendWith(JulToSLF4DelegateExtension.class)
    static class JpaRepositoryValidationTest {

        @Test
        void testAddValidate() {
            testAddEntity();
        }

        @Test
        void testUpdateValidate() {
            testUpdateEntity();
        }
    }

    static void testAddEntity() {
        var rentaiItemRepo = CDI.current().select(RentalItemRepository.class).get();
        testOfAddEntity(rentaiItemRepo , new RentalItem(), 1);

        var reservationRepo = CDI.current().select(ReservationRepository.class).get();
        testOfAddEntity(reservationRepo, new Reservation(), 5);

        var userAccountRepo = CDI.current().select(UserAccountRepository.class).get();
        testOfAddEntity(userAccountRepo, new UserAccount(), 4);
    }

    static void testUpdateEntity() {
        var userAccountRepo = CDI.current().select(UserAccountRepository.class).get();
        testOfUpdateUserAccount(userAccountRepo, new UserAccount(), 5);
    }

    static <T> void testOfAddEntity(GenericRepository<T> repository, T entity, int expectedErrorSize) {

        ConstraintViolationException actual =
                catchThrowableOfType(() ->
                    repository.add(entity),
                    ConstraintViolationException.class
                );
        ConstraintViolationSetAssert.assertThat(actual.getConstraintViolations())
            .hasSize(expectedErrorSize);
    }

    static void testOfUpdateUserAccount(UserAccountRepository repository, UserAccount entity, int expectedErrorSize) {

        ConstraintViolationException actual =
                catchThrowableOfType(() ->
                    repository.update(entity),
                    ConstraintViolationException.class
                );
        ConstraintViolationSetAssert.assertThat(actual.getConstraintViolations())
            .hasSize(expectedErrorSize);
    }
}
