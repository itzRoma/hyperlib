package my.projects.hyperlib.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
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
    @Size(min = 4, max = 255, message = "Username should be 4 or more characters long!")
    private String username;

    @Size(min = 8, max = 255, message = "Password should be 8 or more characters long!")
    private String password;

    @NotEmpty(message = "Firstname cannot be empty!")
    private String firstName;

    @NotEmpty(message = "Lastname cannot be empty!")
    private String lastName;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationDate;

    private String imageUrl;

    private Boolean locked;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "users_roles", joinColumns = @JoinColumn(name = "users_id"))
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