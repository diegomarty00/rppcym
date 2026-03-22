package com.uniovi.sdi.bookspace.validators;

import com.uniovi.sdi.bookspace.entities.Space;
import com.uniovi.sdi.bookspace.services.SpacesService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class AddSpaceValidator implements Validator {

    private final SpacesService spacesService;

    public AddSpaceValidator(SpacesService spacesService) {
        this.spacesService = spacesService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return Space.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Space space = (Space) target;

        // Nombre vacío
        if (space.getName() == null || space.getName().trim().isEmpty()) {
            errors.rejectValue("name", "space.error.empty.name");
        }

        // Capacidad inválida
        if (space.getCapacity() < 1) {
            errors.rejectValue("capacity", "space.error.capacity");
        }

        if (!errors.hasFieldErrors("name")) {  // Solo si el campo no tiene otros errores
            Space existing = spacesService.getSpaceByName(space.getName().trim());
            if (existing != null) {
                errors.rejectValue("name", "space.error.duplicate");
            }
        }


    }
}