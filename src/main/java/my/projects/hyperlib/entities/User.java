package my.projects.hyperlib.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Data
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 25)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    private String firstName;

    private String lastName;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp registrationDate;

    @Column(columnDefinition = "BIT(1) DEFAULT 0")
    private Boolean blocked;

    @ManyToMany
    @JoinTable(name = "user_has_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;
}