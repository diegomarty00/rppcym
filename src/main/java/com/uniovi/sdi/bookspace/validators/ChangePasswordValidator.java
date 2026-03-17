package com.uniovi.sdi.bookspace.validators;

import com.uniovi.sdi.bookspace.entities.ChangePassword;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ChangePasswordValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ChangePassword.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) { //deberia tener validador para que cumpla requisitos?
        ChangePassword form = (ChangePassword) target;

        if (form.getNewPassword() == null || form.getNewPassword().isEmpty()) {
            errors.rejectValue("newPassword", "Error.empty", "La contraseña no puede estar vacía");
        }
        if (!form.getNewPassword().equals(form.getNewPasswordConfirm())) {
            errors.rejectValue("newPasswordConfirm", "password.mismatch", "Las contraseñas no coinciden");
        }
    }
}