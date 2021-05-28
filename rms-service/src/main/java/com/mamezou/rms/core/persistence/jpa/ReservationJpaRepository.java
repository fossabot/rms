package com.mamezou.rms.core.persistence.jpa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import com.mamezou.rms.core.domain.Reservation;
import com.mamezou.rms.core.domain.constraint.ValidationGroups.Add;
import com.mamezou.rms.core.persistence.ReservationRepository;
import com.mamezou.rms.core.persistence.GenericRepository.ApiType;
import com.mamezou.rms.platform.extension.EnabledIfRuntimeConfig;
import com.mamezou.rms.platform.validate.ValidateGroup;
import com.mamezou.rms.platform.validate.ValidateParam;

@ApplicationScoped
@EnabledIfRuntimeConfig(propertyName = ApiType.PROP_NAME, value = ApiType.JPA)
public class ReservationJpaRepository implements ReservationRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Reservation get(int id) {
        return em.find(Reservation.class, id);
    }

    @Override
    public List<Reservation> findByRentalItemAndStartDate(int rentalItemId, LocalDate startDate) {
        var jpql = "select r from Reservation r where r.rentalItemId = ?1 order by r.id";
        return em.createQuery(jpql, Reservation.class)
                    .setParameter(1, rentalItemId)
                    .getResultList().stream()
                    .filter(reservation -> reservation.getStartDateTime().toLocalDate().equals(startDate))
                    .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByReserverId(int reserverId) {
        var jpql = "select r from Reservation r where r.userAccountId = ?1 order by r.id";
        return em.createQuery(jpql, Reservation.class)
                    .setParameter(1, reserverId)
                    .getResultList();
    }

    @Override
    public Reservation findOverlappedReservation(int rentalItemId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        var period = new Reservation.DateTimePeriod(startDateTime, endDateTime);
        var jpql = "select r from Reservation r where r.rentalItemId = ?1 order by r.id";
        return em.createQuery(jpql, Reservation.class)
                    .setParameter(1, rentalItemId)
                    .getResultList().stream()
                    .filter(reservation -> reservation.getReservePeriod().isOverlappedBy(period))
                    .findFirst()
                    .orElse(null);
    }

    @Override
    public List<Reservation> findAll() {
        var jpql = "select r from Reservation r order by r.id";
        return em.createQuery(jpql, Reservation.class)
                    .getResultList();
    }

    @ValidateParam
    @ValidateGroup(groups = Add.class)
    @Override
    public void add(@Valid Reservation entity) {
        em.persist(entity);
        em.flush();
    }

    @Override
    public void delete(Reservation reservation) {
        em.remove(reservation);
        em.flush();
    }
}
