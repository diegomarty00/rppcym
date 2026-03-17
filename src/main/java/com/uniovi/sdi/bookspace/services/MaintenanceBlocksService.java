package com.uniovi.sdi.bookspace.services;

import com.uniovi.sdi.bookspace.entities.BlockStatus;
import com.uniovi.sdi.bookspace.entities.MaintenanceBlock;
import com.uniovi.sdi.bookspace.entities.Reservation;
import com.uniovi.sdi.bookspace.entities.Space;
import com.uniovi.sdi.bookspace.repositories.MaintenanceBlockRepository;
import com.uniovi.sdi.bookspace.repositories.ReservationsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaintenanceBlocksService {

    private final MaintenanceBlockRepository maintenanceBlockRepository;
    private final ReservationsRepository reservationsRepository;

    public MaintenanceBlocksService(MaintenanceBlockRepository maintenanceBlockRepository,
                                    ReservationsRepository reservationsRepository) {
        this.maintenanceBlockRepository = maintenanceBlockRepository;
        this.reservationsRepository = reservationsRepository;
    }

    public MaintenanceBlock getBlock(Long id) {
        return maintenanceBlockRepository.findById(id).orElse(null);
    }

    public List<MaintenanceBlock> getBlocksForSpace(Space space) {
        return maintenanceBlockRepository.findBySpaceOrderByStartDateTimeAsc(space);
    }

    public void createBlock(Space space, MaintenanceBlock block) {
        if (space == null) {
            throw new IllegalArgumentException("El espacio no existe.");
        }
        if (!space.isActive()) {
            throw new IllegalArgumentException("No se puede bloquear un espacio inactivo.");
        }
        if (block.getStartDateTime() == null || block.getEndDateTime() == null) {
            throw new IllegalArgumentException("Debes indicar la fecha de inicio y de fin.");
        }
        if (!block.getStartDateTime().isBefore(block.getEndDateTime())) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la de fin.");
        }
        if (block.getReason() == null || block.getReason().isBlank()) {
            throw new IllegalArgumentException("Debes indicar el motivo del bloqueo.");
        }

        List<MaintenanceBlock> overlappingBlocks =
                maintenanceBlockRepository.findBySpaceAndStatusAndEndDateTimeAfterAndStartDateTimeBefore(
                        space,
                        BlockStatus.ACTIVE,
                        block.getStartDateTime(),
                        block.getEndDateTime()
                );

        if (!overlappingBlocks.isEmpty()) {
            throw new IllegalArgumentException("El bloqueo se solapa con otro bloqueo activo del mismo espacio.");
        }

        List<Reservation> overlappingReservations =
                reservationsRepository.findActiveReservationsInRange(
                        space,
                        block.getStartDateTime(),
                        block.getEndDateTime()
                );

        if (!overlappingReservations.isEmpty()) {
            throw new IllegalArgumentException("El bloqueo se solapa con una reserva activa del mismo espacio.");
        }

        block.setSpace(space);
        block.setStatus(BlockStatus.ACTIVE);
        maintenanceBlockRepository.save(block);
    }

    public void cancelBlock(Long blockId) {
        MaintenanceBlock block = maintenanceBlockRepository.findById(blockId).orElse(null);
        if (block == null) {
            throw new IllegalArgumentException("El bloqueo no existe.");
        }
        if (block.getStatus() == BlockStatus.CANCELLED) {
            return;
        }
        block.setStatus(BlockStatus.CANCELLED);
        maintenanceBlockRepository.save(block);
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