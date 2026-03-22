package com.uniovi.sdi.bookspace.controllers;

import com.uniovi.sdi.bookspace.entities.Reservation;
import com.uniovi.sdi.bookspace.entities.ReservationStatus;
import com.uniovi.sdi.bookspace.entities.Space;
import com.uniovi.sdi.bookspace.entities.User;
import com.uniovi.sdi.bookspace.services.ReservationsService;
import com.uniovi.sdi.bookspace.services.SecurityService;
import com.uniovi.sdi.bookspace.services.SpacesService;
import com.uniovi.sdi.bookspace.services.UsersService;
import com.uniovi.sdi.bookspace.validators.AddReservationValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/reservations")
public class ReservationsController {
    private final ReservationsService reservationsService;
    private final SecurityService securityService;
    private final UsersService usersService;
    private final SpacesService spacesService;
    private final AddReservationValidator addReservationValidator;

    public ReservationsController(ReservationsService reservationsService, SecurityService securityService,
                                  UsersService usersService, SpacesService spacesService,
                                  AddReservationValidator addReservationValidator) {
        this.reservationsService = reservationsService;
        this.securityService = securityService;
        this.usersService = usersService;
        this.spacesService = spacesService;
        this.addReservationValidator = addReservationValidator;
    }

    @GetMapping("/list")
    public String listReservations(@RequestParam(required = false) ReservationStatus status, Model model) {
        String dni = securityService.findLoggedInDni();
        if (dni == null) {
            return "redirect:/login";
        }
        User user = usersService.getByDni(dni);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("reservations", reservationsService.getReservationsForUser(user, status));
        model.addAttribute("status", status);
        return "reservations/list";
    }

    @GetMapping("/list/update")
    public String updateList(@RequestParam(required = false) ReservationStatus status, Model model) {
        String dni = securityService.findLoggedInDni();
        if (dni == null) {
            return "fragments/myReservationsTable :: myReservationsTable";
        }
        User user = usersService.getByDni(dni);
        if (user == null) {
            return "fragments/myReservationsTable :: myReservationsTable";
        }
        model.addAttribute("reservations", reservationsService.getReservationsForUser(user, status));
        return "fragments/myReservationsTable :: myReservationsTable";
    }

    @GetMapping("/add")
    public String showAddForm(@RequestParam(required = false) Long spaceId, Model model) {
        User user = getLoggedUser();
        if (user == null) {
            return "redirect:/login";
        }
        if (!isStandardUser(user)) {
            return "redirect:/";
        }

        if (spaceId != null) {
            Space space = spacesService.getSpace(spaceId);
            if (space == null || !space.isActive()) {
                spaceId = null;
            }
        }
        Reservation reservation = new Reservation();
        Space selectedSpace = new Space();
        if (spaceId != null) {
            selectedSpace.setId(spaceId);
        }
        reservation.setSpace(selectedSpace);
        model.addAttribute("reservation", reservation);
        model.addAttribute("spaces", spacesService.getActiveSpaces(null, null));
        return "reservations/add";
    }

    @PostMapping("/add")
    public String addReservation(@Validated @ModelAttribute("reservation") Reservation reservation,
                                 BindingResult result,
                                 Model model) {
        User user = getLoggedUser();
        if (user == null) {
            return "redirect:/login";
        }
        if (!isStandardUser(user)) {
            return "redirect:/";
        }

        reservation.setUser(user);
        addReservationValidator.validate(reservation, result);
        if (result.hasErrors()) {
            model.addAttribute("spaces", spacesService.getActiveSpaces(null, null));
            return "reservations/add";
        }
        reservationsService.addReservation(reservation);
        return "redirect:/reservations/list";
    }

    @PostMapping("/{id}/cancel")
    public String cancelReservation(@PathVariable Long id) {
        User user = getLoggedUser();
        if (user == null) {
            return "redirect:/login";
        }
        if (!isStandardUser(user)) {
            return "redirect:/";
        }
        reservationsService.cancelReservation(user, id);
        return "redirect:/reservations/list";
    }

    @GetMapping("/admin")
    public String adminReservations(
            @RequestParam(required = false) Long spaceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Page<?> reservationsPage = reservationsService.getGlobalReservations(
                spaceId,
                from,
                to,
                PageRequest.of(page, 5)
        );

        model.addAttribute("reservationsPage", reservationsPage);
        model.addAttribute("spaces", spacesService.getAllSpaces(null, null));
        model.addAttribute("spaceId", spaceId);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        return "reservations/admin";
    }

    @GetMapping("/admin/export")
    public ResponseEntity<String> exportReservations(
            @RequestParam(required = false) Long spaceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        List<Reservation> reservations = reservationsService.getGlobalReservations(spaceId, from, to);
        StringBuilder csv = new StringBuilder();
        csv.append("User,Space,Start,End,Status").append("\n");
        for (Reservation reservation : reservations) {
            csv.append(escapeCsv(reservation.getUser() == null ? "" : reservation.getUser().getDni())).append(",");
            csv.append(escapeCsv(reservation.getSpace() == null ? "" : reservation.getSpace().getName())).append(",");
            csv.append(escapeCsv(formatDateTime(reservation.getStartDateTime()))).append(",");
            csv.append(escapeCsv(formatDateTime(reservation.getEndDateTime()))).append(",");
            csv.append(escapeCsv(reservation.getStatus() == null ? "" : reservation.getStatus().name())).append("\n");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reservations.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.toString());
    }

    private User getLoggedUser() {
        String dni = securityService.findLoggedInDni();
        if (dni == null) {
            return null;
        }
        return usersService.getByDni(dni);
    }

    private boolean isStandardUser(User user) {
        return usersService.getUserRoles()[0].equals(user.getUserRole());
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        boolean needsQuotes = escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")
                || escaped.contains("\r");
        return needsQuotes ? "\"" + escaped + "\"" : escaped;
    }
}
