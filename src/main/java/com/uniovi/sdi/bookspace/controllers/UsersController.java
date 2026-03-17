package com.uniovi.sdi.bookspace.controllers;

import com.uniovi.sdi.bookspace.entities.ChangePassword;
import com.uniovi.sdi.bookspace.entities.User;
import com.uniovi.sdi.bookspace.services.SecurityService;
import com.uniovi.sdi.bookspace.services.UsersService;
import com.uniovi.sdi.bookspace.validators.ChangePasswordValidator;
import com.uniovi.sdi.bookspace.validators.SignUpFormValidator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UsersController {
    private final SignUpFormValidator signUpFormValidator;
    private final ChangePasswordValidator changePasswordValidator;
    private final UsersService usersService;
    private final SecurityService securityService;

    public UsersController(SignUpFormValidator signUpFormValidator, UsersService usersService,
                            SecurityService securityService, ChangePasswordValidator changePasswordValidator) {
        this.signUpFormValidator = signUpFormValidator;
        this.usersService = usersService;
        this.securityService = securityService;
        this.changePasswordValidator = changePasswordValidator;
    }

    @GetMapping(value="/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal())) {

            if (usersService.isAdmin()) return "redirect:/";   // redirigir cuando se implemente a listado global reservas
            else return "redirect:/";    // redirigir cuando se implemente a listado espacios
        }
        return "login";
    }

    @GetMapping(value = "/signup")
    public String signup(Model model){
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping(value="/signup")
    public String signup(@Validated User user, BindingResult result){
        signUpFormValidator.validate(user, result);
        if(result.hasErrors())
            return "signup";
        user.setUserRole(usersService.getUserRoles()[0]);
        usersService.addUser(user);
        securityService.autoLogin(user.getDni(), user.getPasswordConfirm());
        return "redirect:/spaces";
    }

    @GetMapping(value="/user/changePasswd")
    public String changePasswd(Model model){
        if(usersService.isAdmin())
            return "redirect:/";
        model.addAttribute("changePasswordForm", new ChangePassword());
        return "user/changePasswd";
    }

    @PostMapping(value="/user/changePasswd")
    public String changePasswd(@ModelAttribute("changePasswordForm") @Validated ChangePassword form,
                                     BindingResult result) {
        changePasswordValidator.validate(form, result);
        if(result.hasErrors()) {
            return "user/changePasswd";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User activeUser = usersService.getByDni(auth.getName());

        usersService.updatePassword(activeUser, form.getNewPassword());
        return "redirect:/";
    }
}
