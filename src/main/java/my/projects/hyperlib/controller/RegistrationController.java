package my.projects.hyperlib.controller;

import my.projects.hyperlib.entity.User;
import my.projects.hyperlib.exception.NotFoundException;
import my.projects.hyperlib.service.implementation.RoleService;
import my.projects.hyperlib.service.implementation.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
    private UserService userService;
    private RoleService roleService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String showRegistrationForm() {
        return "registration";
    }

    @PostMapping
    public String registerNewUser(User user, Model model) {
        try {
            User userFromDb = userService.findByUsername(user.getUsername());
            model.addAttribute("message", "User '" + userFromDb.getUsername() + "' is already exists");
            return "registration";
        } catch (NotFoundException ex) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setImageUrl("/img/user/defaultProfileImage.png");
            user.setRegistrationDate(new Timestamp(new Date().getTime()));
            user.setLocked(Boolean.FALSE);
            user.setRoles(Collections.singleton(roleService.findByName("ROLE_USER")));
            userService.save(user);

            return "redirect:/login";
        }
    }
}
