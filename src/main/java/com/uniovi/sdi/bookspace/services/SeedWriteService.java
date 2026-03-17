package com.uniovi.sdi.bookspace.services;

import com.uniovi.sdi.bookspace.entities.MaintenanceBlock;
import com.uniovi.sdi.bookspace.entities.Reservation;
import com.uniovi.sdi.bookspace.repositories.MaintenanceBlockRepository;
import com.uniovi.sdi.bookspace.repositories.ReservationsRepository;
import org.springframework.stereotype.Service;

@Service
public class SeedWriteService {
    private final ReservationsRepository reservationsRepository;
    private final MaintenanceBlockRepository maintenanceBlockRepository;

    public SeedWriteService(ReservationsRepository reservationsRepository, MaintenanceBlockRepository maintenanceBlockRepository) {
        this.reservationsRepository = reservationsRepository;
        this.maintenanceBlockRepository = maintenanceBlockRepository;
    }

    public void saveReservation(Reservation reservation) {
        reservationsRepository.save(reservation);
    }

    public void saveBlock(MaintenanceBlock block) {
        maintenanceBlockRepository.save(block);
    }
}