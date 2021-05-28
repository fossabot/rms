package com.mamezou.rms.core.persistence.jpa;

import static com.mamezou.rms.test.assertj.ToStringAssert.*;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.mamezou.rms.core.TestUtils;
import com.mamezou.rms.core.domain.Reservation;
import com.mamezou.rms.core.persistence.AbstractReservationRepositoryTest;
import com.mamezou.rms.core.persistence.ReservationRepository;
import com.mamezou.rms.test.junit5.JpaTransactionalExtension;
import com.mamezou.rms.test.junit5.TransactionalForTest;

@ExtendWith(JpaTransactionalExtension.class)
public class ReservationJpaRepositoryTest extends AbstractReservationRepositoryTest {

    private ReservationJpaRepository repository;

    @BeforeEach
    void setup(EntityManager em) {
        repository = new ReservationJpaRepository();
        TestUtils.setFieldValue(repository, "em", em);
    }

    @Test
    @TransactionalForTest
    void testAdd() {
        var addEntity = Reservation.ofTransient(LocalDateTime.of(2020, 4, 2, 10, 0, 0), LocalDateTime.of(2020, 4, 2, 12, 0, 0), "メモ4", 3, 1);
        repository.add(addEntity);

        addEntity.setId(4);
        var expect = addEntity;
        var actual = repository.get(4);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Override
    protected ReservationRepository repository() {
        return repository;
    }
}
