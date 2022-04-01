package my.projects.hyperlib.controllers;

import my.projects.hyperlib.entities.Role;
import my.projects.hyperlib.entities.User;
import my.projects.hyperlib.exceptions.NotFoundException;
import my.projects.hyperlib.services.RoleService;
import my.projects.hyperlib.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String findOne(
            @PathVariable String username,
            @RequestParam(name = "action", required = false, defaultValue = "view") String action,
            Principal principal,
            Model model
    ) {
        User requestedUser = userService.findByUsername(username);
        if (requestedUser == null) {
            throw new NotFoundException("user '" + username + "' not found");
        }
        model.addAttribute("user", requestedUser);

        if (checkIfUserIsAdmin(principal.getName()) && action.equals("edit")) {
            model.addAttribute("roles", roleService.findAll());
            return "user/userEdit";
        }

        if (principal.getName().equals(username) && action.equals("profileEdit")) {
            return "user/userProfileEdit";
        }

        return "user/user";
    }

    @PostMapping("/{username}/edit")
    public String userEdit(
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
        userService.save(userToEdit);

        return "redirect:/users";
    }

    @PostMapping("/{username}/profileEdit")
    public String profileEdit(
            @PathVariable String username,
            @RequestParam Map<String, String> form,
            Principal principal
    ) {
        User userToEdit = userService.findByUsername(username);

        String newFirstName = form.get("firstName").trim();
        String newLastName = form.get("lastName").trim();
        String newUsername = form.get("username").trim();
        String newPassword = form.get("password").trim();

        if (!newFirstName.isEmpty()) {
            userToEdit.setFirstName(newFirstName);
        }
        if (!newLastName.isEmpty()) {
            userToEdit.setLastName(newLastName);
        }
        if (!newPassword.isEmpty()) {
            userToEdit.setPassword(passwordEncoder.encode(newPassword));
        }
        if (!newUsername.isEmpty() && !newUsername.equals(username)) {
            userToEdit.setUsername(newUsername);
            userService.save(userToEdit);

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
        User userToDelete = userService.findByUsername(username);
        userService.delete(userToDelete);

        return "redirect:/users";
    }

    private boolean checkIfUserIsAdmin(String username) {
        return userService.findByUsername(username).getRoles().contains(roleService.findByName("ROLE_ADMIN"));
    }
}
