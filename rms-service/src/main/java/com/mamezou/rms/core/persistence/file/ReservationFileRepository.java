package com.mamezou.rms.core.persistence.file;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.mamezou.rms.core.domain.Reservation;
import com.mamezou.rms.core.persistence.GenericRepository.ApiType;
import com.mamezou.rms.core.persistence.ReservationRepository;
import com.mamezou.rms.core.persistence.file.converter.EntityArrayConverter;
import com.mamezou.rms.core.persistence.file.io.FileAccessor;
import com.mamezou.rms.platform.extension.EnabledIfRuntimeConfig;

@ApplicationScoped
@EnabledIfRuntimeConfig(propertyName = ApiType.PROP_NAME, value = ApiType.FILE)
public class ReservationFileRepository extends AbstractFileRepository<Reservation> implements ReservationRepository {

    @Inject
    public ReservationFileRepository(FileAccessor fileAccessor, EntityArrayConverter<Reservation> converter) {
        super(fileAccessor, converter);
    }

    @Override
    public List<Reservation> findByRentalItemAndStartDate(int rentalItemId, LocalDate startDate) {
        return this.load().stream()
                .map(this.getConverter()::toEntity)
                .filter(reservation -> reservation.getStartDateTime().toLocalDate().equals(startDate))
                .filter(reservation -> reservation.getRentalItemId() == rentalItemId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByReserverId(int reserverId) {
        return this.load().stream()
                .map(this.getConverter()::toEntity)
                .filter(reservation -> reservation.getUserAccountId() == reserverId)
                .collect(Collectors.toList());
    }

    @Override
    public Reservation findOverlappedReservation(int rentalItemId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        var conditionOfPeriod = new Reservation.DateTimePeriod(startDateTime, endDateTime);
        return this.load().stream()
                .map(this.getConverter()::toEntity)
                .filter(reservation -> reservation.getRentalItemId() == rentalItemId)
                .filter(reservation -> reservation.getReservePeriod().isOverlappedBy(conditionOfPeriod))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void delete(Reservation reservation) {
        this.delete(reservation.getId());
    }
}
