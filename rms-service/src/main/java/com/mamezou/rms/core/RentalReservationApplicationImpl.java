package com.mamezou.rms.core;

import static com.mamezou.rms.core.exception.BusinessFlowException.CauseType.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.mamezou.rms.core.common.LoginUserUtils;
import com.mamezou.rms.core.domain.RentalItem;
import com.mamezou.rms.core.domain.Reservation;
import com.mamezou.rms.core.domain.UserAccount;
import com.mamezou.rms.core.exception.BusinessFlowException;
import com.mamezou.rms.core.exception.RmsSystemException;
import com.mamezou.rms.core.service.RentalItemService;
import com.mamezou.rms.core.service.ReservationService;
import com.mamezou.rms.core.service.UserAccountService;

@Transactional(TxType.REQUIRED)
@ApplicationScoped
public class RentalReservationApplicationImpl implements RentalReservationApplication {

    private RentalItemService rentalItemService;
    private ReservationService reservationService;
    private UserAccountService userService;

    private Map<Class<?>, Function<Integer, ?>> entityGetterMap;

    // ----------------------------------------------------- constructor methods

    @Inject
    public RentalReservationApplicationImpl(RentalItemService rentalItemService,
                ReservationService reservationService,
                UserAccountService userAccountService) {
        this.rentalItemService = rentalItemService;
        this.reservationService = reservationService;
        this.userService = userAccountService;

        this.entityGetterMap = Map.of(
                RentalItem.class, rentalItemService::get,
                Reservation.class, reservationService::get,
                UserAccount.class, userAccountService::get
            );
    }


    // ----------------------------------------------------- public methods

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> entityClass, int id) {
        return (T) entityGetterMap.get(entityClass).apply(id);
    }

    @Override
    public UserAccount authenticate(String loginId, String password) throws BusinessFlowException {
        var user = userService.findByLoginIdAndPasswod(loginId, password);
        if (user == null) {
            throw new BusinessFlowException("The loginId or password is different", NOT_FOUND);
        }
        return user;
    }

    @Override
    public List<Reservation> findReservationByRentalItemAndStartDate(Integer rentalItemId, LocalDate startDate)
            throws BusinessFlowException {
        var reservations = reservationService.findByRentalItemAndStartDate(rentalItemId, startDate);
        if (reservations.isEmpty()) {
            throw new BusinessFlowException("Reservation does not exist for rentalItemId and startDate", NOT_FOUND);
        }
        return reservations.stream()
                .map(this::toTraversedReservation)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findReservationByReserverId(int reserverId) {
        var reservations = reservationService.findByReserverId(reserverId);
        return reservations.stream()
                .map(this::toTraversedReservation)
                .collect(Collectors.toList());
    }

    @Override
    public List<RentalItem> getAllRetailItems() {
        return rentalItemService.findAll();
    }

    @Override
    public List<UserAccount> getAllUserAccounts() {
        return userService.findAll();
    }

    @Override
    public Reservation addReservation(Reservation addReservation) throws BusinessFlowException {

        var rentalItem = rentalItemService.get(addReservation.getRentalItemId());
        if (rentalItem == null) {
            throw new BusinessFlowException("RentalItem does not exist for rentalItemId.", NOT_FOUND);
        }

        // 予約の登録
        Reservation newReservation = reservationService.add(addReservation);

        // 予約オブジェクトの再構成
        newReservation.setRentalItem(rentalItem);
        UserAccount reserver = getUserAccount(addReservation.getUserAccountId());
        newReservation.setUserAccount(reserver);

        return newReservation;
    }

    @Override
    public RentalItem addRentalItem(RentalItem addRentalItem) throws BusinessFlowException, RmsSystemException {
        return rentalItemService.add(addRentalItem);
    }

    @Override
    public UserAccount addUserAccount(UserAccount addUserAccount) throws BusinessFlowException {
        return userService.add(addUserAccount);
    }

    @Override
    public void cancelReservation(int reservationId) throws BusinessFlowException {
        reservationService.cancel(reservationId, LoginUserUtils.get().getUserId());
    }

    @Override
    public UserAccount updateUserAccount(UserAccount updateUserAccount) {
        return userService.update(updateUserAccount);
    }


    // ----------------------------------------------------- private methods

    private Reservation toTraversedReservation(Reservation resavation) {
        RentalItem rentalItems = getRentalItem(resavation.getRentalItemId());
        resavation.setRentalItem(rentalItems);
        UserAccount reservers = getUserAccount(resavation.getUserAccountId());
        resavation.setUserAccount(reservers);
        return resavation;
    }

    private RentalItem getRentalItem(int rentalItemId) {
        return rentalItemService.get(rentalItemId);
    }

    private UserAccount getUserAccount(int userAccountId) {
        return userService.get(userAccountId);
    }
}
