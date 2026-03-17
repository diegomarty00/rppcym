package com.uniovi.sdi.bookspace.services;

import com.uniovi.sdi.bookspace.entities.Space;
import com.uniovi.sdi.bookspace.repositories.SpacesRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
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

    public List<Space> getSpaces(){
        List<Space> spaces = new ArrayList<>();
        spacesRepository.findAll();
        return spaces;
    }

    public Space getSpace(Long id){
        return spacesRepository.findById(id).orElse(null);
    }

    public Space addSpace(Space space){
        return spacesRepository.save(space);
    }

    public void delete(Long id){
        spacesRepository.deleteById(id);
    }

    /**
    public Page<Space> getSpacesPage(Pageable pageable) {
        return spacesRepository.findAll(pageable);
    }

    public Page<Space> searchSpacesByNameAndType(Pageable pageable, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return spacesRepository.findAll(pageable);
        }
        return spacesRepository.searchByNameOrType(pageable, "%" + searchText.toLowerCase() + "%");
    }
 */

    public List<Space> getVisibleSpaces(String type, Integer minCapacity) {
        if (usersService.isAdmin()) {
            return getAllSpaces(type, minCapacity);
        }
        return getActiveSpaces(type, minCapacity);
    }

    public List<Space> getAllSpaces(String type, Integer minCapacity) {
        return spacesRepository.findAll().stream()
                .filter(space -> type == null || type.isBlank() || space.getType().equalsIgnoreCase(type))
                .filter(space -> minCapacity == null || space.getCapacity() >= minCapacity)
                .toList();
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

}
