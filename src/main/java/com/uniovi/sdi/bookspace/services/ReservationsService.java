package com.uniovi.sdi.bookspace.services;

import com.uniovi.sdi.bookspace.entities.Reservation;
import com.uniovi.sdi.bookspace.entities.RecurrenceFrequency;
import com.uniovi.sdi.bookspace.entities.ReservationStatus;
import com.uniovi.sdi.bookspace.entities.User;
import com.uniovi.sdi.bookspace.repositories.ReservationsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationsService {
    private final ReservationsRepository reservationsRepository;
    private final AvailabilityService availabilityService;

    public ReservationsService(ReservationsRepository reservationsRepository, AvailabilityService availabilityService) {
        this.reservationsRepository = reservationsRepository;
        this.availabilityService = availabilityService;
    }

    public List<Reservation> getReservationsForUser(User user, ReservationStatus status) {
        if (status == null) {
            return reservationsRepository.findByUserOrderByStartDateTimeAsc(user);
        }
        return reservationsRepository.findByUserAndStatusOrderByStartDateTimeAsc(user, status);
    }

    public void addReservation(Reservation reservation) {
        reservation.setReason(normalizeReason(reservation.getReason()));
        reservationsRepository.save(reservation);
        createRecurringReservations(reservation);
    }

    public void cancelReservation(User user, Long reservationId) {
        Reservation reservation = reservationsRepository.findById(reservationId).orElse(null);
        if (reservation == null || user == null) {
            return;
        }
        if (!reservation.getUser().getDni().equals(user.getDni())) {
            return;
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            return;
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationsRepository.save(reservation);
    }



    public Page<Reservation> getGlobalReservations(Long spaceId,
                                                   LocalDateTime from,
                                                   LocalDateTime to,
                                                   Pageable pageable) {

        from = from == null ? LocalDateTime.of(1970, 1, 1, 0, 0) : from;
        to = to == null ? LocalDateTime.of(2100, 1, 1, 23, 59) : to;

        if (from.isAfter(to)) {
            LocalDateTime temp = from;
            from = to;
            to = temp;
        }

        return reservationsRepository.findGlobalFilteredPage(spaceId, from, to, pageable);
    }

    private String normalizeReason(String reason) {
        if (reason == null) {
            return null;
        }
        String trimmed = reason.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void createRecurringReservations(Reservation baseReservation) {
        if (baseReservation.getRecurrenceFrequency() == null ||
                baseReservation.getRecurrenceFrequency() == RecurrenceFrequency.NONE ||
                baseReservation.getRecurrenceEndDate() == null) {
            return;
        }

        LocalDateTime nextStart = move(baseReservation.getStartDateTime(), baseReservation.getRecurrenceFrequency());
        LocalDateTime nextEnd = move(baseReservation.getEndDateTime(), baseReservation.getRecurrenceFrequency());
        while (!nextStart.toLocalDate().isAfter(baseReservation.getRecurrenceEndDate())) {
            Reservation recurringReservation = new Reservation(
                    baseReservation.getUser(),
                    baseReservation.getSpace(),
                    nextStart,
                    nextEnd,
                    baseReservation.getReason()
            );
            reservationsRepository.save(recurringReservation);
            nextStart = move(nextStart, baseReservation.getRecurrenceFrequency());
            nextEnd = move(nextEnd, baseReservation.getRecurrenceFrequency());
        }
    }

    private LocalDateTime move(LocalDateTime dateTime, RecurrenceFrequency recurrenceFrequency) {
        return switch (recurrenceFrequency) {
            case DAILY -> dateTime.plusDays(1);
            case WEEKLY -> dateTime.plusWeeks(1);
            case MONTHLY -> dateTime.plusMonths(1);
            case YEARLY -> dateTime.plusYears(1);
            case NONE -> dateTime;
        };
    }
}
