package com.itzroma.hyperlib.service.implementation;

import com.itzroma.hyperlib.entity.User;
import com.itzroma.hyperlib.exception.NotFoundException;
import com.itzroma.hyperlib.repository.UserRepository;
import com.itzroma.hyperlib.entity.Role;
import com.itzroma.hyperlib.service.CommonServiceContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService, CommonServiceContract<User> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //    Method which Spring requires for authentication

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userFromDB = findByUsername(username);
        if (userFromDB == null)
            throw new UsernameNotFoundException(String.format("User '%s' not found!", username));

        return new org.springframework.security.core.userdetails.User(
                userFromDB.getUsername(),
                userFromDB.getPassword(),
                true, true, true,
                !userFromDB.getLocked(),
                mapRolesToAuthorities(userFromDB.getRoles())
        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }

//    Business logic

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void save(User entity) {
        userRepository.save(entity);
    }

    @Override
    public void delete(User entity) {
        userRepository.delete(entity);
    }

    @Override
    public void update(User source, User target) {
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());

        if (source.getPassword() != null && !source.getPassword().isEmpty())
            target.setPassword(passwordEncoder.encode(source.getPassword()));

        if (!source.getUsername().equals(target.getUsername())) {
            target.setUsername(source.getUsername());
            SecurityContextHolder.clearContext();
        }

        userRepository.save(target);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(String.format("User '%s' not found!", username)));
    }

    public List<User> findByRole(Role role) {
        return new ArrayList<>(userRepository.findByRole(role));
    }

    public boolean checkIfUsernameIsAvailable(String username) {
        return userRepository.findByUsername(username).isEmpty();
    }
}