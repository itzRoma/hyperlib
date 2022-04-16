package my.projects.hyperlib.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank(message = "Username cannot contain spaces!")
    @Size(min = 4, max = 255, message = "Username should be 4 or more characters long!")
    private String username;

    @NotBlank(message = "Password cannot contain spaces!")
    @Size(min = 8, max = 255, message = "Password should be 8 or more characters long!")
    private String password;

    @NotBlank(message = "Firstname cannot be empty!")
    private String firstName;

    @NotBlank(message = "Lastname cannot be empty!")
    private String lastName;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationDate;

    private String imageUrl;

    private Boolean locked;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"))
    private Set<Role> roles;

    public User(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @PrePersist
    public void prePersist() {
        imageUrl = "/img/common/defaultProfileImage.png";
        locked = false;
        roles = Collections.singleton(Role.USER);
    }
}