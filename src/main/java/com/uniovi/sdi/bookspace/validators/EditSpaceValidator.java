package com.uniovi.sdi.bookspace.validators;

import com.uniovi.sdi.bookspace.entities.Space;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class EditSpaceValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Space.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Space space = (Space) target;

        if (space.getName() == null || space.getName().trim().isEmpty()) {
            errors.rejectValue("name", "space.error.empty.name");
        }

        if (space.getCapacity() < 1) {
            errors.rejectValue("capacity", "space.error.capacity");
        }
    }
}