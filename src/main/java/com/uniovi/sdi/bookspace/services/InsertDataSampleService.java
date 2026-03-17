package com.uniovi.sdi.bookspace.services;

import com.uniovi.sdi.bookspace.entities.MaintenanceBlock;
import com.uniovi.sdi.bookspace.entities.Reservation;
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
        User user1 = new User("10000001S", "Luis", "Ortega", "Us3r@1-PASSW", "ROLE_USER");
        User user2 = new User("10000002Q", "Ana", "Ramos", "Us3r@2-PASSW", "ROLE_USER");
        User user3 = new User("12345678Z", "Pedro", "Díaz", "@Dm1n1str@D0r", "ROLE_ADMIN");
        User user4 = new User("77777777Y", "Lucas", "Núñez", "ClaveSegura#2026", "ROLE_USER");
        User user5 = new User("12345678Q", "Juan", "Rodriguez", "Sol1!Luz7@Mar", "ROLE_USER");
        usersService.addUser(user1);
        usersService.addUser(user2);
        usersService.addUser(user3);
        usersService.addUser(user4);
        usersService.addUser(user5);

        Space space1 = new Space("Sala Azul", "Reunión", "Planta 1", 6, true);
        Space space2 = new Space("Aula 101", "Aula", "Edificio Norte", 30, true);
        Space space3 = new Space("Cowork 3", "Cowork", "Planta 2", 1, true);
        spacesService.addSpace(space1);
        spacesService.addSpace(space2);
        spacesService.addSpace(space3);

        LocalDateTime today9 = LocalDateTime.now().withHour(9).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime today11 = today9.plusHours(2);
        LocalDateTime today14 = today9.plusHours(5);
        LocalDateTime today16 = today9.plusHours(7);

        seedWriteService.saveReservation(new Reservation(user1, space1, today9, today11, "Reunión de equipo"));
        seedWriteService.saveReservation(new Reservation(user1, space2, today14, today16, "Formación"));
        seedWriteService.saveBlock(new MaintenanceBlock(space1, today11, today14, "Mantenimiento"));
    }
}