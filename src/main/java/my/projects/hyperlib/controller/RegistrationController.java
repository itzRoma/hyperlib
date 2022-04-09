package my.projects.hyperlib.controller;

import my.projects.hyperlib.entity.User;
import my.projects.hyperlib.exception.NotFoundException;
import my.projects.hyperlib.service.implementation.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String showRegistrationForm(@ModelAttribute("newUser") User newUser) {
        return "registration";
    }

    @PostMapping
    public String registerNewUser(
            @ModelAttribute("newUser") @Valid User newUser,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }

        try {
            User userFromDb = userService.findByUsername(newUser.getUsername());
            model.addAttribute("error", String.format("User '%s' is already exists!", userFromDb.getUsername()));
            return "registration";
        } catch (NotFoundException ex) {
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            userService.save(newUser);
            return "redirect:/login";
        }
    }
}
