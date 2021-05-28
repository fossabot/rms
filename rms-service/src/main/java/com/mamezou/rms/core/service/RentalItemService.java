package com.mamezou.rms.core.service;

import static com.mamezou.rms.core.exception.BusinessFlowException.CauseType.*;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.mamezou.rms.core.domain.RentalItem;
import com.mamezou.rms.core.exception.BusinessFlowException;
import com.mamezou.rms.core.persistence.RentalItemRepository;

@ApplicationScoped
public class RentalItemService {

    private RentalItemRepository repository;

    // ----------------------------------------------------- constructor methods

    @Inject
    public RentalItemService(RentalItemRepository rentalItemRepository) {
        this.repository = rentalItemRepository;
    }

    // ----------------------------------------------------- public methods

    public RentalItem get(int rentalItemId) {
        return repository.get(rentalItemId);
    }

    public List<RentalItem> findAll() {
        return repository.findAll();
    }

    public RentalItem findBySerialNo(String serialNo) {
        return repository.findBySerialNo(serialNo);
    }

    public RentalItem add(RentalItem addRentalItem) {
        // シリアル番号の重複チェック
        if (findBySerialNo(addRentalItem.getSerialNo()) != null) {
            throw new BusinessFlowException("The serialNo is already registered.", DUPRICATE);
        }
        // 登録
        repository.add(addRentalItem);
        return get(addRentalItem.getId());
    }
}
