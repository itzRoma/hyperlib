package my.projects.hyperlib.controllers;

import my.projects.hyperlib.entities.Role;
import my.projects.hyperlib.entities.User;
import my.projects.hyperlib.exceptions.NotFoundException;
import my.projects.hyperlib.services.RoleService;
import my.projects.hyperlib.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String findAll(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users";
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

        if (checkIfUserIsAdmin(principal) && action.equals("edit")) {
            model.addAttribute("roles", roleService.findAll());
            return "userEdit";
        }
        return "user";
    }

    @PostMapping("/{username}")
    public String userEdit(
            @PathVariable String username,
            @RequestParam(name = "blockUnblockUsername", required = false) String blockUnblockUsername,
            @RequestParam(name = "deleteUsername", required = false) String deleteUsername,
            @RequestParam Map<String, String> form
    ) {
        if (blockUnblockUsername != null) {
            User blockUnblockUser = userService.findByUsername(blockUnblockUsername);
            if (blockUnblockUser.getBlocked()) {
                blockUnblockUser.setBlocked(Boolean.FALSE);
            } else {
                blockUnblockUser.setBlocked(Boolean.TRUE);
            }
            userService.save(blockUnblockUser);
        } else if (deleteUsername != null) {
            User userToDelete = userService.findByUsername(deleteUsername);
            userService.delete(userToDelete);
        } else {
            User userToEdit = userService.findByUsername(username);

            Set<String> roles = roleService.findAll().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());

            userToEdit.getRoles().clear();

            for (String key : form.keySet()) {
                if (roles.contains(key)) {
                    userToEdit.getRoles().add(roleService.findByName(key));
                }
                if (key.equals("blocked")) {
                    userToEdit.setBlocked(Boolean.TRUE);
                } else {
                    userToEdit.setBlocked(Boolean.FALSE);
                }
            }

            userService.save(userToEdit);
        }

        return "redirect:/users";
    }

    private boolean checkIfUserIsAdmin(Principal principal) {
        return userService.findByUsername(principal.getName()).getRoles().contains(roleService.findByName("ROLE_ADMIN"));
    }
}
