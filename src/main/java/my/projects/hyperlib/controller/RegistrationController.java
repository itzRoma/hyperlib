package my.projects.hyperlib.controller;

import my.projects.hyperlib.entity.User;
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
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
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
        newUser.setFirstName(newUser.getFirstName().trim());
        newUser.setLastName(newUser.getLastName().trim());
        newUser.setUsername(newUser.getUsername().trim());
        newUser.setPassword(newUser.getPassword().trim());

        if (!userService.checkIfUsernameIsAvailable(newUser.getUsername())) {
            model.addAttribute("error", String.format("Username '%s' is taken!", newUser.getUsername()));
            return "registration";
        }

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userService.save(newUser);
        return "redirect:/login";
    }
}
