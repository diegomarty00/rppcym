package com.uniovi.sdi.bookspace.controllers;

import com.uniovi.sdi.bookspace.entities.ReservationStatus;
import com.uniovi.sdi.bookspace.entities.User;
import com.uniovi.sdi.bookspace.services.ReservationsService;
import com.uniovi.sdi.bookspace.services.SecurityService;
import com.uniovi.sdi.bookspace.services.SpacesService;
import com.uniovi.sdi.bookspace.services.UsersService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/reservations")
public class ReservationsController {
    private final ReservationsService reservationsService;
    private final SecurityService securityService;
    private final UsersService usersService;
    private final SpacesService spacesService;

    public ReservationsController(ReservationsService reservationsService, SecurityService securityService,
                                  UsersService usersService, SpacesService spacesService) {
        this.reservationsService = reservationsService;
        this.securityService = securityService;
        this.usersService = usersService;
        this.spacesService = spacesService;
    }

    @GetMapping("/mine")
    public String listMine(@RequestParam(required = false) ReservationStatus status, Model model) {
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
        return "reservations/mine";
    }

    @GetMapping("/mine/update")
    public String updateMine(@RequestParam(required = false) ReservationStatus status, Model model) {
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
}
