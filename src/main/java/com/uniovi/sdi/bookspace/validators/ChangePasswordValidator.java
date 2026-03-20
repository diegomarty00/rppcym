package com.uniovi.sdi.bookspace.validators;

import com.uniovi.sdi.bookspace.entities.User;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ChangePasswordValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            errors.rejectValue("password", "Error.empty", "La contraseña no puede estar vacía");
        }
        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            errors.rejectValue("passwordConfirm", "password.mismatch", "Las contraseñas no coinciden");
        }
    }
}