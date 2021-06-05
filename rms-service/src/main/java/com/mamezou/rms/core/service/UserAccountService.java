package com.mamezou.rms.core.service;

import static com.mamezou.rms.core.exception.BusinessFlowException.CauseType.*;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.mamezou.rms.core.domain.UserAccount;
import com.mamezou.rms.core.exception.BusinessFlowException;
import com.mamezou.rms.core.persistence.UserAccountRepository;

@ApplicationScoped
public class UserAccountService {

    private UserAccountRepository repository;

    // ----------------------------------------------------- constructor methods

    @Inject
    public UserAccountService(UserAccountRepository userRepository) {
        this.repository = userRepository;
    }

    // ----------------------------------------------------- public methods

    public UserAccount get(int userId) {
        return repository.get(userId);
    }

    public List<UserAccount> findAll() {
        return repository.findAll();
    }

    public UserAccount findByLoginIdAndPasswod(String loginId, String password) {
        return repository.findByLoginIdAndPasswod(loginId, password);
    }


    public UserAccount findByLoginId(String loginId) {
        return repository.findByLoginId(loginId);
    }

    public UserAccount add(UserAccount addUserAccount) throws BusinessFlowException {

        // ログイン名の重複チェック
        var existingUserAccount = this.findByLoginId(addUserAccount.getLoginId());
        if (existingUserAccount != null) {
            throw new BusinessFlowException("loginId is already registered.", DUPRICATE);
        }

        // 登録
        repository.add(addUserAccount);
        return get(addUserAccount.getId());
    }

    public UserAccount update(UserAccount updateUserAccount) throws BusinessFlowException {
        var updatedUser = repository.update(updateUserAccount);
        if (updatedUser == null) {
            throw new BusinessFlowException("UserAccount does not exist for userAccountId", NOT_FOUND);
        }
        return updatedUser;
    }
}
