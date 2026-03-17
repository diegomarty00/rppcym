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

    public Page<Reservation> getGlobalReservations(Long spaceId,
                                                   LocalDateTime from,
                                                   LocalDateTime to,
                                                   Pageable pageable) {
        return reservationsRepository.findGlobalFilteredPage(spaceId, from, to, pageable);
    }
}