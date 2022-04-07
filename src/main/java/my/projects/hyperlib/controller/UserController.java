package my.projects.hyperlib.controller;

import my.projects.hyperlib.entity.Role;
import my.projects.hyperlib.entity.User;
import my.projects.hyperlib.service.implementation.RoleService;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UserController {
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
        model.addAttribute("roles", roleService.findAll());
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

        Set<String> roles = roleService.findAll().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        userToEdit.getRoles().clear();

        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                userToEdit.getRoles().add(roleService.findByName(key));
            }
        }

        if (userToEdit.getRoles().isEmpty()) {
            userToEdit.getRoles().add(roleService.findByName("ROLE_USER"));
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
                !newPassword.isEmpty()
        ) {
            String passwordError = null;
            if (newPassword.length() < 8 || newPassword.length() > 255) {
                passwordError = "Password should be 8 or more characters long!";
            }
            model.addAttribute("passwordError", passwordError);

            return "user/userProfileEdit";
        }

        User userToEdit = userService.findByUsername(username);

        userToEdit.setFirstName(user.getFirstName());
        userToEdit.setLastName(user.getLastName());

        if (!newPassword.equals("")) {
            userToEdit.setPassword(passwordEncoder.encode(newPassword));
        }

        if (!userToEdit.getUsername().equals(username)) {
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
}
