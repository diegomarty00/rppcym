package com.uniovi.sdi.bookspace.services;

import com.uniovi.sdi.bookspace.entities.MaintenanceBlock;
import com.uniovi.sdi.bookspace.entities.Reservation;
import com.uniovi.sdi.bookspace.entities.ReservationStatus;
import com.uniovi.sdi.bookspace.entities.Space;
import com.uniovi.sdi.bookspace.entities.User;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InsertDataSampleService {
    private final UsersService usersService;
    private final SpacesService spacesService;
    private final SeedWriteService seedWriteService;

    public InsertDataSampleService(UsersService usersService, SpacesService spacesService, SeedWriteService seedWriteService) {
        this.usersService = usersService;
        this.spacesService = spacesService;
        this.seedWriteService = seedWriteService;
    }

    @PostConstruct
    public void init() {
        User user1 = new User("10000001S", "Luis", "Ortega", "Us3r@1-PASSW", usersService.getUserRoles()[0]);
        User user2 = new User("10000002Q", "Ana", "Ramos", "Us3r@2-PASSW", usersService.getUserRoles()[0]);
        User user3 = new User("12345678Z", "Pedro", "Diaz", "@Dm1n1str@D0r", usersService.getUserRoles()[1]);
        User user4 = new User("77777777Y", "Lucas", "Nunez", "ClaveSegura#2026", usersService.getUserRoles()[0]);
        User user5 = new User("12345678Q", "Juan", "Rodriguez", "Sol1!Luz7@Mar", usersService.getUserRoles()[0]);
        usersService.addUser(user1);
        usersService.addUser(user2);
        usersService.addUser(user3);
        usersService.addUser(user4);
        usersService.addUser(user5);

        Space space1 = new Space("Sala Azul", "Sala", "Planta 1", 6, true);
        Space space2 = new Space("Aula 101", "Aula", "Edificio Norte", 30, true);
        Space space3 = new Space("Cowork 3", "Cowork", "Planta 2", 1, true);
        spacesService.addSpace(space1);
        spacesService.addSpace(space2);
        spacesService.addSpace(space3);

        LocalDateTime tomorrow9 = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime tomorrow11 = tomorrow9.plusHours(2);
        LocalDateTime tomorrow14 = tomorrow9.plusHours(5);
        LocalDateTime tomorrow16 = tomorrow9.plusHours(7);

        seedWriteService.saveReservation(new Reservation(user1, space2, tomorrow14, tomorrow16, "Formacion"));
        seedWriteService.saveBlock(new MaintenanceBlock(space1, tomorrow11, tomorrow14, "Mantenimiento"));

        seedWriteService.saveReservation(
                new Reservation(user1, space1, tomorrow9, tomorrow9.plusHours(2), "Reunion 1")
        );
        seedWriteService.saveReservation(
                new Reservation(user1, space1, tomorrow9.plusDays(1), tomorrow9.plusDays(1).plusHours(2), "Reunion 2")
        );
        seedWriteService.saveReservation(
                new Reservation(user2, space1, tomorrow9.plusDays(2), tomorrow9.plusDays(2).plusHours(2), "Reunion 3")
        );

        seedWriteService.saveReservation(
                new Reservation(user2, space2, tomorrow9.plusHours(3), tomorrow9.plusHours(5), "Clase 1")
        );
        seedWriteService.saveReservation(
                new Reservation(user4, space2, tomorrow9.plusDays(3), tomorrow9.plusDays(3).plusHours(2), "Clase 2")
        );
        seedWriteService.saveReservation(
                new Reservation(user4, space2, tomorrow9.plusDays(4), tomorrow9.plusDays(4).plusHours(3), "Clase 3")
        );

        seedWriteService.saveReservation(
                new Reservation(user5, space3, tomorrow9.plusHours(1), tomorrow9.plusHours(3), "Trabajo individual")
        );
        seedWriteService.saveReservation(
                new Reservation(user5, space3, tomorrow9.plusDays(2), tomorrow9.plusDays(2).plusHours(2), "Trabajo 2")
        );

        Reservation cancelled = new Reservation(user1, space1,
                tomorrow9.plusDays(5),
                tomorrow9.plusDays(5).plusHours(1),
                "Reserva cancelada");
        cancelled.setStatus(ReservationStatus.CANCELLED);
        seedWriteService.saveReservation(cancelled);

        seedWriteService.saveBlock(
                new MaintenanceBlock(space1,
                        tomorrow9.plusHours(2),
                        tomorrow9.plusHours(4),
                        "Limpieza rapida")
        );

        seedWriteService.saveBlock(
                new MaintenanceBlock(space1,
                        tomorrow9.plusDays(2).plusHours(2),
                        tomorrow9.plusDays(2).plusHours(4),
                        "Mantenimiento programado")
        );

        seedWriteService.saveBlock(
                new MaintenanceBlock(space2,
                        tomorrow9.plusDays(1).plusHours(1),
                        tomorrow9.plusDays(1).plusHours(3),
                        "Averia equipo")
        );
    }
}
