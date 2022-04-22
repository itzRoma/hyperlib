package my.projects.hyperlib.controller;

import my.projects.hyperlib.entity.Role;
import my.projects.hyperlib.entity.User;
import my.projects.hyperlib.service.implementation.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String findAllUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/users";
    }

    @GetMapping("/{username}")
    public String findOneUser(@PathVariable String username, Model model) {
        model.addAttribute("user", userService.findByUsername(username));
        return "user/user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{username}/edit")
    public String showUserEditForm(@PathVariable String username, Model model) {
        model.addAttribute("editedUser", userService.findByUsername(username));
        model.addAttribute("roles", Arrays.asList(Role.ADMIN, Role.LIBRARIAN));
        return "user/userEditForm";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{username}/edit")
    public String editUser(
            @PathVariable String username,
            @ModelAttribute("editedUser") User editedUser,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors("roles")) {
            return "user/userEditForm";
        }

        User userToEdit = userService.findByUsername(username);
        Set<Role> roles = new HashSet<>(Collections.singleton(Role.USER));

        roles.addAll(editedUser.getRoles());
        userToEdit.setRoles(roles);
        userService.save(userToEdit);

        return "redirect:/users";
    }

    @GetMapping("/{username}/profileEdit")
    public String showUserProfileEditForm(@PathVariable String username, Principal principal, Model model) {
        if (principal.getName().equals(username)) {
            model.addAttribute("editedUser", userService.findByUsername(username));
            return "user/userProfileEditForm";
        }
        return "redirect:/users";
    }

    @PostMapping("/{username}/profileEdit")
    public String editUserProfile(
            @PathVariable String username,
            @ModelAttribute("editedUser") @Valid User editedUser,
            BindingResult bindingResult,
            Model model
    ) {
        editedUser.setFirstName(editedUser.getFirstName().trim());
        editedUser.setLastName(editedUser.getLastName().trim());
        editedUser.setUsername(editedUser.getUsername().trim());

        if (!username.equals(editedUser.getUsername()) && !userService.checkIfUsernameIsAvailable(editedUser.getUsername())) {
            model.addAttribute("editedUser", userService.findByUsername(username));
            model.addAttribute("usernameError", String.format("Username '%s' is taken!", editedUser.getUsername()));
            return "user/userProfileEditForm";
        }

        if (bindingResult.hasErrors()) {
            return "user/userProfileEditForm";
        }

        User userToEdit = userService.findByUsername(username);
        userService.update(editedUser, userToEdit);
        return "redirect:/users/" + userToEdit.getUsername();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{username}/lockUnlock")
    public String lockUnlockUser(@PathVariable String username) {
        User lockUnlockUser = userService.findByUsername(username);

        if (lockUnlockUser.getLocked()) {
            lockUnlockUser.setLocked(Boolean.FALSE);
        } else {
            lockUnlockUser.setLocked(Boolean.TRUE);
        }
        userService.save(lockUnlockUser);

        return "redirect:/users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{username}/delete")
    public String deleteUser(@PathVariable String username) {
        userService.delete(userService.findByUsername(username));
        return "redirect:/users";
    }
}
