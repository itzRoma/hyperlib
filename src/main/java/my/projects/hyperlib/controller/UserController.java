package my.projects.hyperlib.controller;

import my.projects.hyperlib.entity.Role;
import my.projects.hyperlib.entity.User;
import my.projects.hyperlib.exception.NotFoundException;
import my.projects.hyperlib.service.implementation.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/users")
public class UserController {
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
    @PreAuthorize("hasRole('ADMIN')")
    public String findAll(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/users";
    }

    @GetMapping("/{username}")
    public String findOne(@PathVariable String username, Model model) {
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "user/user";
    }

    @GetMapping("/{username}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showUserEditForm(@PathVariable String username, Model model) {
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "user/userEdit";
    }

    @GetMapping("/{username}/profileEdit")
    public String showProfileEditForm(@PathVariable String username, Principal principal, Model model) {
        if (principal.getName().equals(username)) {
            User user = userService.findByUsername(username);
            model.addAttribute("user", user);
            return "user/userProfileEdit";
        }

        return "redirect:/users";
    }

    @PostMapping("/{username}/edit")
    public String edit(
            @PathVariable String username,
            @RequestParam Map<String, String> form
    ) {
        User userToEdit = userService.findByUsername(username);

        Collection<String> roles = Stream.of(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        userToEdit.getRoles().clear();

        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                userToEdit.getRoles().add(Role.valueOf(key));
            }
        }

        if (userToEdit.getRoles().isEmpty()) {
            userToEdit.getRoles().add(Role.USER);
        }

        userService.save(userToEdit);

        return "redirect:/users";
    }

    @PostMapping("/{username}/profileEdit")
    public String profileEdit(
            @PathVariable String username,
            @ModelAttribute("user") @Valid User user,
            BindingResult bindingResult,
            @RequestParam(name = "newPassword", required = false) String newPassword,
            Model model
    ) {
        if (
                bindingResult.hasFieldErrors("firstName") ||
                        bindingResult.hasFieldErrors("lastName") ||
                        bindingResult.hasFieldErrors("username") ||
                        checkIfPasswordIsIncorrect(newPassword)
        ) {
            if (checkIfPasswordIsIncorrect(newPassword)) {
                model.addAttribute("passwordError", "Password should be 8 or more characters long!");
            }
            return "user/userProfileEdit";
        }

        User userToEdit = userService.findByUsername(username);

        userToEdit.setFirstName(user.getFirstName());
        userToEdit.setLastName(user.getLastName());

        if (!newPassword.isEmpty()) {
            userToEdit.setPassword(passwordEncoder.encode(newPassword));
        }

        if (!userToEdit.getUsername().equals(user.getUsername())) {
            userToEdit.setUsername(user.getUsername());
            SecurityContextHolder.clearContext();
        }

        userService.save(userToEdit);

        return "redirect:/users/" + userToEdit.getUsername();
    }

    @PostMapping("/{username}/lockUnlock")
    public String lockUnlock(@PathVariable String username) {
        User lockUnlockUser = userService.findByUsername(username);

        if (lockUnlockUser.getLocked()) {
            lockUnlockUser.setLocked(Boolean.FALSE);
        } else {
            lockUnlockUser.setLocked(Boolean.TRUE);
        }
        userService.save(lockUnlockUser);

        return "redirect:/users";
    }

    @PostMapping("/{username}/delete")
    public String delete(@PathVariable String username) {
        userService.delete(userService.findByUsername(username));
        return "redirect:/users";
    }

    private boolean checkIfPasswordIsIncorrect(String password) {
        String trimmedPassword = password.trim();
        return !trimmedPassword.isEmpty() && (trimmedPassword.length() < 8 || trimmedPassword.length() > 255);
    }
}
