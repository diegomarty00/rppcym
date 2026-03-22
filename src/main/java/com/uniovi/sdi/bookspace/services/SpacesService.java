package com.uniovi.sdi.bookspace.services;

import com.uniovi.sdi.bookspace.entities.Space;
import com.uniovi.sdi.bookspace.repositories.SpacesRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class SpacesService {
    private final SpacesRepository spacesRepository;
    private final UsersService usersService;

    public SpacesService(SpacesRepository spacesRepository, UsersService usersService) {
        this.spacesRepository = spacesRepository;
        this.usersService = usersService;
    }

    public List<Space> getActiveSpaces(String typeFilter, Integer minCapacity) {
        List<Space> spaces = spacesRepository.findByActiveTrue();
        if (typeFilter != null && !typeFilter.isBlank()) {
            String normalized = typeFilter.toLowerCase(Locale.ROOT);
            spaces = spaces.stream()
                    .filter(space -> space.getType() != null &&
                            space.getType().toLowerCase(Locale.ROOT).contains(normalized))
                    .collect(Collectors.toList());
        }
        if (minCapacity != null) {
            spaces = spaces.stream()
                    .filter(space -> space.getCapacity() >= minCapacity)
                    .collect(Collectors.toList());
        }
        return spaces;
    }

    public List<Space> getSpaces() {
        List<Space> spaces = new ArrayList<>();
        spacesRepository.findAll().forEach(spaces::add);
        return spaces;
    }

    public List<Space> getVisibleSpaces(String typeFilter, Integer minCapacity) {
        if (usersService.isAdmin()) {
            return getAllSpaces(typeFilter, minCapacity);
        }
        return getActiveSpaces(typeFilter, minCapacity);
    }

    public List<Space> getAllSpaces(String typeFilter, Integer minCapacity) {
        List<Space> spaces = spacesRepository.findAll();
        if (typeFilter != null && !typeFilter.isBlank()) {
            String normalized = typeFilter.toLowerCase(Locale.ROOT);
            spaces = spaces.stream()
                    .filter(space -> space.getType() != null &&
                            space.getType().toLowerCase(Locale.ROOT).contains(normalized))
                    .collect(Collectors.toList());
        }
        if (minCapacity != null) {
            spaces = spaces.stream()
                    .filter(space -> space.getCapacity() >= minCapacity)
                    .collect(Collectors.toList());
        }
        return spaces;
    }

    public Space getSpace(Long id) {
        return spacesRepository.findById(id).orElse(null);
    }

    public void addSpace(Space space) {
        spacesRepository.save(space);
    }

    public void delete(Long id) {
        spacesRepository.deleteById(id);
    }


    public Space getSpaceByName(String name) {
        return spacesRepository.findByName(name);
    }

}
