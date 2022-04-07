package my.projects.hyperlib.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Username cannot be empty!")
    @Size(min = 3, max = 255, message = "Username is too short!")
    @Column(unique = true)
    private String username;

    @NotEmpty(message = "Password cannot be empty!")
    @Size(min = 8, max = 255, message = "Password should be 8 or more characters long!")
    private String password;

    @NotEmpty(message = "Firstname cannot be empty!")
    @Size(min = 1, max = 255, message = "Firstname should be 1 or more characters long!")
    private String firstName;

    @NotEmpty(message = "Lastname cannot be empty!")
    @Size(min = 1, max = 255, message = "Lastname should be 1 or more characters long!")
    private String lastName;

    private String imageUrl;

    private Timestamp registrationDate;

    private Boolean locked;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_has_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;
}