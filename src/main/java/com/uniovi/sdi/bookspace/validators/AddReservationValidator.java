package com.uniovi.sdi.bookspace.validators;

import com.uniovi.sdi.bookspace.entities.Reservation;
import com.uniovi.sdi.bookspace.entities.RecurrenceFrequency;
import com.uniovi.sdi.bookspace.entities.Space;
import com.uniovi.sdi.bookspace.services.AvailabilityService;
import com.uniovi.sdi.bookspace.services.ReservationsService;
import com.uniovi.sdi.bookspace.services.SpacesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
public class AddReservationValidator implements Validator {

    private final AvailabilityService availabilityService;
    private final SpacesService spacesService;
    private final ReservationsService reservationsService;
    private final int maxActiveReservations;

    public AddReservationValidator(AvailabilityService availabilityService,
                                   SpacesService spacesService,
                                   ReservationsService reservationsService,
                                   @Value("${reservations.active.limit:3}") int maxActiveReservations) {
        this.availabilityService = availabilityService;
        this.spacesService = spacesService;
        this.reservationsService = reservationsService;
        this.maxActiveReservations = maxActiveReservations;
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
        } else {
            long activeCount = reservationsService.countActiveReservationsForUser(
                    reservation.getUser(),
                    LocalDateTime.now()
            );
            int plannedReservations = countPlannedReservations(reservation);
            if (activeCount + plannedReservations > maxActiveReservations) {
                errors.reject("reservations.add.error.limit");
            }
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

        if (reservation.getRecurrenceFrequency() == null) {
            reservation.setRecurrenceFrequency(RecurrenceFrequency.NONE);
        }

        if (reservation.getRecurrenceFrequency() != RecurrenceFrequency.NONE) {
            if (reservation.getRecurrenceEndDate() == null) {
                errors.rejectValue("recurrenceEndDate", "reservations.add.error.recurrenceEnd.required");
            } else if (reservation.getRecurrenceEndDate().isBefore(startDateTime.toLocalDate())) {
                errors.rejectValue("recurrenceEndDate", "reservations.add.error.recurrenceEnd.range");
            }
        }

        if (space == null || errors.hasFieldErrors("startDateTime") || errors.hasFieldErrors("recurrenceEndDate")) {
            return;
        }

        if (!availabilityService.getActiveReservationsInRange(space, startDateTime, endDateTime).isEmpty()) {
            errors.reject("reservations.add.error.overlap");
        }

        if (!availabilityService.getActiveBlocksInRange(space, startDateTime, endDateTime).isEmpty()) {
            errors.reject("reservations.add.error.blocked");
        }

        if (errors.hasErrors() || reservation.getRecurrenceFrequency() == RecurrenceFrequency.NONE ||
                reservation.getRecurrenceEndDate() == null) {
            return;
        }

        LocalDateTime nextStartDateTime = move(startDateTime, reservation.getRecurrenceFrequency());
        LocalDateTime nextEndDateTime = move(endDateTime, reservation.getRecurrenceFrequency());
        while (!nextStartDateTime.toLocalDate().isAfter(reservation.getRecurrenceEndDate())) {
            if (!availabilityService.getActiveReservationsInRange(space, nextStartDateTime, nextEndDateTime).isEmpty()) {
                errors.reject("reservations.add.error.overlap");
                return;
            }
            if (!availabilityService.getActiveBlocksInRange(space, nextStartDateTime, nextEndDateTime).isEmpty()) {
                errors.reject("reservations.add.error.blocked");
                return;
            }
            nextStartDateTime = move(nextStartDateTime, reservation.getRecurrenceFrequency());
            nextEndDateTime = move(nextEndDateTime, reservation.getRecurrenceFrequency());
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

    private int countPlannedReservations(Reservation reservation) {
        if (reservation == null || reservation.getStartDateTime() == null
                || reservation.getEndDateTime() == null) {
            return 0;
        }

        if (reservation.getRecurrenceFrequency() == null ||
                reservation.getRecurrenceFrequency() == RecurrenceFrequency.NONE ||
                reservation.getRecurrenceEndDate() == null) {
            return 1;
        }

        int count = 1;
        LocalDateTime nextStartDateTime = move(reservation.getStartDateTime(), reservation.getRecurrenceFrequency());
        while (!nextStartDateTime.toLocalDate().isAfter(reservation.getRecurrenceEndDate())) {
            count++;
            nextStartDateTime = move(nextStartDateTime, reservation.getRecurrenceFrequency());
        }
        return count;
    }
}
