package my.projects.hyperlib.controllers;

import my.projects.hyperlib.entities.User;
import my.projects.hyperlib.exceptions.NotFoundException;
import my.projects.hyperlib.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String findAll(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users";
    }

    @GetMapping("/{username}")
    public String findOne(@PathVariable String username, Model model) {
        User requestedUser = userService.findByUsername(username);
        if (requestedUser == null) {
            throw new NotFoundException("user '" + username + "' not found");
        }

        model.addAttribute("user", requestedUser);
        return "user";
    }
}
