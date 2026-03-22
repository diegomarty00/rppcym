package com.uniovi.sdi.bookspace.validators;

import com.uniovi.sdi.bookspace.entities.User;
import org.passay.*;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
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

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "Error.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordConfirm", "Error.empty");

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            PasswordValidator validator = new PasswordValidator(
                    new LengthRule(12, 20),
                    new CharacterRule(EnglishCharacterData.UpperCase, 1),
                    new CharacterRule(EnglishCharacterData.LowerCase, 1),
                    new CharacterRule(EnglishCharacterData.Digit, 1),
                    new CharacterRule(EnglishCharacterData.Special, 1),
                    new WhitespaceRule()
            );
            RuleResult result = validator.validate(new PasswordData(user.getPassword()));
            if (!result.isValid()) {
                errors.rejectValue("password", "Error.signup.password.invalid");
            }
        }

        if (user.getPassword() != null && !user.getPassword().equals(user.getPasswordConfirm())) {
            errors.rejectValue("passwordConfirm", "password.mismatch", "Las contraseñas no coinciden");
        }
    }
}
