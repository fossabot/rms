package com.mamezou.rms.core.persistence.file;

import static com.mamezou.rms.core.TestUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.mamezou.rms.core.TestUtils.PathResolverParameterExtension;
import com.mamezou.rms.core.domain.RentalItem;
import com.mamezou.rms.core.persistence.AbstractRentalItemRepositoryTest;
import com.mamezou.rms.core.persistence.RentalItemRepository;
import com.mamezou.rms.core.persistence.file.io.PathResolver;

@ExtendWith(PathResolverParameterExtension.class)
public class RentalItemFileRepositoryTest extends AbstractRentalItemRepositoryTest {

    private RentalItemFileRepository repository;

    @BeforeEach
    void setUp(PathResolver pathResolver) throws Exception {
        repository = newRentalItemFileRepository(pathResolver);
    }

    @Test
    void testAdd() throws Exception {

        RentalItem addRentalItem = RentalItem.ofTransient("A0005", "レンタル品5号");

        repository.add(addRentalItem);

        List<String[]> records = getAllRecords(repository.getStoragePath());
        String[] lastRecord = (String[]) records.get(records.size() - 1);
        String[] expectRow = { "-1", "A0005", "レンタル品5号" };
        expectRow[0] = String.valueOf(records.size());

        assertThat(lastRecord).containsExactly(expectRow);
    }

    @Override
    protected RentalItemRepository repository() {
        return repository;
    }
}
