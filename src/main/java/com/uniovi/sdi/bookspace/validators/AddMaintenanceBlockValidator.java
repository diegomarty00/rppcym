package com.uniovi.sdi.bookspace.validators;

import com.uniovi.sdi.bookspace.entities.BlockStatus;
import com.uniovi.sdi.bookspace.entities.MaintenanceBlock;
import com.uniovi.sdi.bookspace.entities.Reservation;
import com.uniovi.sdi.bookspace.entities.Space;
import com.uniovi.sdi.bookspace.repositories.MaintenanceBlockRepository;
import com.uniovi.sdi.bookspace.repositories.ReservationsRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class AddMaintenanceBlockValidator implements Validator {

    private final MaintenanceBlockRepository maintenanceBlockRepository;
    private final ReservationsRepository reservationsRepository;

    public AddMaintenanceBlockValidator(MaintenanceBlockRepository maintenanceBlockRepository,
                                        ReservationsRepository reservationsRepository) {
        this.maintenanceBlockRepository = maintenanceBlockRepository;
        this.reservationsRepository = reservationsRepository;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return MaintenanceBlock.class.equals(aClass);
    }


    @Override
    public void validate(Object target, Errors errors) {

        MaintenanceBlock block = (MaintenanceBlock) target;

        Space space = block.getSpace();

        // --- VALIDACIONES BÁSICAS ---
        if (space == null) {
            errors.rejectValue("startDateTime", "blocks.add.error.space.invalid");
            return;
        }

        if (!space.isActive()) {
            errors.rejectValue("startDateTime", "blocks.add.error.space.inactive");
        }

        if (block.getStartDateTime() == null) {
            errors.rejectValue("startDateTime", "blocks.add.error.start.required");
        }
        if (block.getEndDateTime() == null) {
            errors.rejectValue("endDateTime", "blocks.add.error.end.required");
        }
        if (block.getReason() == null || block.getReason().isBlank()) {
            errors.rejectValue("reason", "blocks.add.error.reason.required");
        }

        // Si ya hay errores, no seguimos
        if (errors.hasErrors()) return;

        // --- VALIDACIÓN DE RANGO ---
        if (!block.getStartDateTime().isBefore(block.getEndDateTime())) {
            errors.rejectValue("startDateTime", "blocks.add.error.range");
            return;
        }

        // --- SOLAPAMIENTO CON BLOQUEOS ---
        List<MaintenanceBlock> overlappingBlocks =
                maintenanceBlockRepository.findActiveOverlappingBlocks(
                        space.getId(),
                        block.getStartDateTime(),
                        block.getEndDateTime()
                );

        if (!overlappingBlocks.isEmpty()) {
            errors.rejectValue("startDateTime", "blocks.add.error.overlap.block");
        }

        // --- SOLAPAMIENTO CON RESERVAS ---
        List<Reservation> overlappingReservations =
                reservationsRepository.findActiveReservationsInRange(
                        space,
                        block.getStartDateTime(),
                        block.getEndDateTime()
                );

        if (!overlappingReservations.isEmpty()) {
            errors.rejectValue("startDateTime", "blocks.add.error.overlap.reservation");
        }


    }
}
