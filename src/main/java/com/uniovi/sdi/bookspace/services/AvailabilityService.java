package com.uniovi.sdi.bookspace.services;

import com.uniovi.sdi.bookspace.entities.BlockStatus;
import com.uniovi.sdi.bookspace.entities.MaintenanceBlock;
import com.uniovi.sdi.bookspace.entities.Reservation;
import com.uniovi.sdi.bookspace.entities.ReservationStatus;
import com.uniovi.sdi.bookspace.entities.Space;
import com.uniovi.sdi.bookspace.repositories.MaintenanceBlockRepository;
import com.uniovi.sdi.bookspace.repositories.ReservationsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AvailabilityService {
    private final ReservationsRepository reservationsRepository;
    private final MaintenanceBlockRepository maintenanceBlockRepository;

    public AvailabilityService(ReservationsRepository reservationsRepository, MaintenanceBlockRepository maintenanceBlockRepository) {
        this.reservationsRepository = reservationsRepository;
        this.maintenanceBlockRepository = maintenanceBlockRepository;
    }

    public List<Reservation> getActiveReservationsInRange(Space space, LocalDateTime from, LocalDateTime to) {
        return reservationsRepository.findBySpaceAndStatusAndEndDateTimeAfterAndStartDateTimeBefore(
                space,
                ReservationStatus.ACTIVE,
                from,
                to
        );
    }

    public List<MaintenanceBlock> getActiveBlocksInRange(Space space, LocalDateTime from, LocalDateTime to) {
        return maintenanceBlockRepository.findBySpaceAndStatusAndEndDateTimeAfterAndStartDateTimeBefore(
                space,
                BlockStatus.ACTIVE,
                from,
                to
        );
    }
}