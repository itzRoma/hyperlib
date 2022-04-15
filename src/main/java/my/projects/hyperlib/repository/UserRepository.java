package my.projects.hyperlib.repository;

import my.projects.hyperlib.entity.Role;
import my.projects.hyperlib.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query(value = "SELECT * FROM users INNER JOIN users_roles ON users.id = users_roles.user_id WHERE users_roles.roles = :role", nativeQuery = true)
    Collection<User> findByRole(@Param("role") Role role);
}