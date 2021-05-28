package com.mamezou.rms.core.persistence;

import static com.mamezou.rms.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.mamezou.rms.core.domain.RentalItem;

public abstract class AbstractRentalItemRepositoryTest {

    protected abstract RentalItemRepository repository();

    @Test
    void testGet() {
        var expect = RentalItem.of(1, "A0001", "レンタル品1号");
        var actual = repository().get(1);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testFindAll() {
        var expect = List.of(
                RentalItem.of(1, "A0001", "レンタル品1号"),
                RentalItem.of(2, "A0002", "レンタル品2号"),
                RentalItem.of(3, "A0003", "レンタル品3号"),
                RentalItem.of(4, "A0004", "レンタル品4号")
            );
        var actual = repository().findAll();
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }

    @Test
    void testFindBySerialNo() {
        var expect = RentalItem.of(3, "A0003", "レンタル品3号");
        var actual = repository().findBySerialNo("A0003");
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testFindBySerialNoNotFound() {
        var actual = repository().findBySerialNo("A9999");
        assertThat(actual).isNull();
    }
}
