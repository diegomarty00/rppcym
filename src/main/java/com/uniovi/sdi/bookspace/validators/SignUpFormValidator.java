package com.uniovi.sdi.bookspace.validators;

import com.uniovi.sdi.bookspace.entities.User;
import com.uniovi.sdi.bookspace.services.UsersService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.passay.*;

@Component
public class SignUpFormValidator implements Validator {

    private final UsersService usersService;

    public SignUpFormValidator(UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        // Campos obligatorios
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dni", "Error.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "Error.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "Error.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "Error.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordConfirm", "Error.empty");

        // DNI válido y único
        String dni = user.getDni();
        if (!dni.matches("\\d{8}[A-Za-z]"))  // 8 dígitos + letra
            errors.rejectValue("dni", "Error.signup.dni.invalid");
        if (usersService.getByDni(dni) != null)
            errors.rejectValue("dni", "Error.signup.dni.duplicate");

        // Nombre y Apellidos longitud mínima y máxima
        if ((user.getName().length() < 3 || user.getName().length() > 24)) {
            errors.rejectValue("name", "Error.signup.name.length");
        }
        if ((user.getLastName().length() < 3 || user.getLastName().length() > 50)) {
            errors.rejectValue("lastName", "Error.signup.lastName.length");
        }

        // Coinciden password y passwordConfirm
        if (!user.getPassword().equals(user.getPasswordConfirm()))
            errors.rejectValue("passwordConfirm", "Error.signup.passwordConfirm.coincidence");

        // Contraseña segura con Passay
        PasswordValidator validator = new PasswordValidator(
                new LengthRule(12, 20),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new WhitespaceRule()
        );
        RuleResult result = validator.validate(new PasswordData(user.getPassword()));
        if(!result.isValid())
            errors.rejectValue("password", "Error.signup.password.invalid");
    }
}
