package com.mamezou.rms.core.persistence.file.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.mamezou.rms.core.domain.Reservation;

public class ReservationArrayConverter implements EntityArrayConverter<Reservation> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    public static final ReservationArrayConverter INSTANCE = new ReservationArrayConverter();

    @Override
    public Reservation toEntity(String[] attributes) {

        int reservationId = Integer.parseInt(attributes[0]);
        LocalDateTime startDateTime = LocalDateTime.parse(attributes[1], DATE_TIME_FORMATTER);
        LocalDateTime endDateTime = LocalDateTime.parse(attributes[2], DATE_TIME_FORMATTER);
        String note = attributes[3];
        int rentalItemId = Integer.parseInt(attributes[4]);
        int userAccountId = Integer.parseInt(attributes[5]);

        return Reservation.of(reservationId, startDateTime, endDateTime, note, rentalItemId, userAccountId);
    }

    @Override
    public String[] toArray(Reservation reservation) {

        String[] reservationAttributes = new String[6];

        reservationAttributes[0] = String.valueOf(reservation.getId());
        reservationAttributes[1] = DATE_TIME_FORMATTER.format(reservation.getStartDateTime());
        reservationAttributes[2] = DATE_TIME_FORMATTER.format(reservation.getEndDateTime());
        reservationAttributes[3] = reservation.getNote();
        reservationAttributes[4] = String.valueOf(reservation.getRentalItemId());
        reservationAttributes[5] = String.valueOf(reservation.getUserAccountId());

        return reservationAttributes;
    }
}
