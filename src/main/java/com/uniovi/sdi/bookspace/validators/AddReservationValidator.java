package com.uniovi.sdi.bookspace.validators;

import com.uniovi.sdi.bookspace.entities.Reservation;
import com.uniovi.sdi.bookspace.entities.Space;
import com.uniovi.sdi.bookspace.services.AvailabilityService;
import com.uniovi.sdi.bookspace.services.SpacesService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
public class AddReservationValidator implements Validator {

    private final AvailabilityService availabilityService;
    private final SpacesService spacesService;

    public AddReservationValidator(AvailabilityService availabilityService, SpacesService spacesService) {
        this.availabilityService = availabilityService;
        this.spacesService = spacesService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Reservation.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Reservation reservation = (Reservation) target;

        if (reservation.getUser() == null) {
            errors.reject("reservations.add.error.user");
        }

        if (reservation.getSpace() == null || reservation.getSpace().getId() == null) {
            errors.rejectValue("space.id", "reservations.add.error.space.required");
        }
        Space space = null;
        if (!errors.hasFieldErrors("space.id")) {
            space = spacesService.getSpace(reservation.getSpace().getId());
            if (space == null) {
                errors.rejectValue("space.id", "reservations.add.error.space.invalid");
            } else if (!space.isActive()) {
                errors.rejectValue("space.id", "reservations.add.error.space.inactive");
            } else {
                reservation.setSpace(space);
            }
        }

        if (reservation.getStartDateTime() == null) {
            errors.rejectValue("startDateTime", "reservations.add.error.start.required");
        }

        if (reservation.getEndDateTime() == null) {
            errors.rejectValue("endDateTime", "reservations.add.error.end.required");
        }

        LocalDateTime startDateTime = reservation.getStartDateTime();
        LocalDateTime endDateTime = reservation.getEndDateTime();
        if (startDateTime == null || endDateTime == null) {
            return;
        }

        if (!startDateTime.isBefore(endDateTime)) {
            errors.rejectValue("startDateTime", "reservations.add.error.range");
        }

        if (startDateTime.isBefore(LocalDateTime.now())) {
            errors.rejectValue("startDateTime", "reservations.add.error.past");
        }

        if (space == null || errors.hasFieldErrors("startDateTime")) {
            return;
        }

        if (!availabilityService.getActiveReservationsInRange(space, startDateTime, endDateTime).isEmpty()) {
            errors.reject("reservations.add.error.overlap");
        }

        if (!availabilityService.getActiveBlocksInRange(space, startDateTime, endDateTime).isEmpty()) {
            errors.reject("reservations.add.error.blocked");
        }
    }
}
