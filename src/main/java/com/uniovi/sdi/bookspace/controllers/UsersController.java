package com.uniovi.sdi.bookspace.controllers;

import com.uniovi.sdi.bookspace.entities.User;
import com.uniovi.sdi.bookspace.services.SecurityService;
import com.uniovi.sdi.bookspace.services.UsersService;
import com.uniovi.sdi.bookspace.validators.ChangePasswordValidator;
import com.uniovi.sdi.bookspace.validators.SignUpFormValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

            if (usersService.isAdmin()) return "redirect:/reservations/admin";
            else return "redirect:/space/list";
        }
        return "login";
    }

    @GetMapping(value = "/signup")
    public String signup(Model model){
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping(value="/signup")
    public String signup(@Validated User user, BindingResult result,
                         HttpServletRequest request, HttpServletResponse response){
        signUpFormValidator.validate(user, result);
        if(result.hasErrors())
            return "signup";
        user.setUserRole(usersService.getUserRoles()[0]);
        usersService.addUser(user);
        securityService.autoLogin(user.getDni(), user.getPasswordConfirm(), request, response);
        return "redirect:/space/list";
    }

    @GetMapping(value="/user/changePasswd")
    public String changePasswd(Model model){
        if(usersService.isAdmin())
            return "redirect:/";
        model.addAttribute("user", new User());
        return "user/changePasswd";
    }

    @PostMapping(value="/user/changePasswd")
    public String changePasswd(@ModelAttribute("changePasswordForm") @Validated User userNewPassword,
                                     BindingResult result) {
        changePasswordValidator.validate(userNewPassword, result);
        if(result.hasErrors()) {
            return "user/changePasswd";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User activeUser = usersService.getByDni(auth.getName());

        usersService.updatePassword(activeUser, userNewPassword.getPassword());
        return "redirect:/";
    }
}
