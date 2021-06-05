package com.mamezou.rms.core.service;

import static com.mamezou.rms.core.exception.BusinessFlowException.CauseType.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.mamezou.rms.core.domain.Reservation;
import com.mamezou.rms.core.exception.BusinessFlowException;
import com.mamezou.rms.core.persistence.ReservationRepository;

@ApplicationScoped
public class ReservationService {

    private ReservationRepository repository;

    // ----------------------------------------------------- constructor methods

    @Inject
    public ReservationService(ReservationRepository reservationRepository) {
        this.repository = reservationRepository;
    }


    // ----------------------------------------------------- public methods

    public List<Reservation> findByRentalItemAndStartDate(int rentalItemId, LocalDate startDate) {
        return repository.findByRentalItemAndStartDate(rentalItemId, startDate);
    }

    public List<Reservation> findByReserverId(int reserverId) {
        return repository.findByReserverId(reserverId);
    }

    public Reservation findOverlappedReservation(int rentalItemId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return repository.findOverlappedReservation(rentalItemId, startDateTime, endDateTime);
    }

    public Reservation get(int reservationId) {
        return repository.get(reservationId);
    }

    public Reservation add(Reservation addReservation) throws BusinessFlowException {
        // 重複チェック
        var reservation = findOverlappedReservation(
                addReservation.getRentalItemId(),
                addReservation.getStartDateTime(),
                addReservation.getEndDateTime());
        if (reservation != null) {
            throw new BusinessFlowException("Already reserved.", DUPRICATE);
        }
        // 登録
        repository.add(addReservation);
        return this.get(addReservation.getId());
    }


    public void cancel(int reservationId, int cancelUserId) throws BusinessFlowException {
        var reservation = repository.get(reservationId);
        if (reservation == null) {
            throw new BusinessFlowException("Reservation does not exist for reservationId", NOT_FOUND);
        }
        // キャンセルはレンタル品を予約した人しか取り消せないことのチェック
        if (reservation.getUserAccountId() != cancelUserId) {
            throw new BusinessFlowException(
                    String.format("Others' reservations cannot be deleted. reserverId=%s, cancelUserId=%s",
                            reservation.getUserAccountId(),
                            cancelUserId),
                    FORBIDDEN);
        }
        repository.delete(reservation);
    }
}
