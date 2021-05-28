package com.mamezou.rms.core.persistence.file;

import static com.mamezou.rms.core.TestUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.mamezou.rms.core.TestUtils.PathResolverParameterExtension;
import com.mamezou.rms.core.domain.UserAccount;
import com.mamezou.rms.core.domain.UserAccount.UserType;
import com.mamezou.rms.core.persistence.AbstractUserAccountRepositoryTest;
import com.mamezou.rms.core.persistence.UserAccountRepository;
import com.mamezou.rms.core.persistence.file.io.PathResolver;

@ExtendWith(PathResolverParameterExtension.class)
public class UserAccountFileRepositoryTest extends AbstractUserAccountRepositoryTest {

    private UserAccountFileRepository repository;

    @BeforeEach
    void setUp(PathResolver pathResolver) throws Exception {
        repository = newUserAccountFileRepository(pathResolver);
    }

    @Test
    void testAdd() throws Exception {
        var addUser = UserAccount.ofTransient("member3", "member3", "メンバー3", "050-1111-2222", "連絡先4", UserType.MEMBER);
        repository.add(addUser);

        List<String[]> records = getAllRecords(repository.getStoragePath());
        String[] lastRecord = records.get(records.size() - 1);
        String[] expectRow = { String.valueOf(records.size()), "member3", "member3", "メンバー3", "050-1111-2222", "連絡先4", "MEMBER" };

        assertThat(lastRecord).containsExactly(expectRow);
    }

    @Override
    protected UserAccountRepository repository() {
        return repository;
    }
}
