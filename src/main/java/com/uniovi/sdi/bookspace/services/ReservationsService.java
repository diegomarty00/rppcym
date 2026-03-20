package com.uniovi.sdi.bookspace.services;

import com.uniovi.sdi.bookspace.entities.Reservation;
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

    public ReservationsService(ReservationsRepository reservationsRepository) {
        this.reservationsRepository = reservationsRepository;
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
        return reservationsRepository.findGlobalFilteredPage(spaceId, from, to, pageable);
    }

    private String normalizeReason(String reason) {
        if (reason == null) {
            return null;
        }
        String trimmed = reason.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
